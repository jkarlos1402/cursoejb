/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.entity.Auction;
import auctionsystem.entity.Bid;
import auctionsystem.entity.Item;
import auctionsystem.entity.User;
import auctionsystem.interceptor.AuctionInterceptor;
import exception.CloseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.ConcurrencyManagement;
import static javax.ejb.ConcurrencyManagementType.CONTAINER;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TimedObject;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Humberto
 */
@TransactionManagement(TransactionManagementType.CONTAINER)
@ConcurrencyManagement(CONTAINER)
@Startup
@Singleton
@Interceptors(AuctionInterceptor.class)
public class StartAuctionBean implements StartAuctionBeanRemote/*, TimedObject*/ {

    @PersistenceContext
    private EntityManager em;

    //Una manera de inyectar timerService
    @Resource
    private TimerService timerService;

    //Otra forma de inyectar un sessionContext, para después hacer una tarea programada
    @Resource
    private SessionContext sessionContext;

    @PostConstruct
    public void initialize() {
        System.out.println("StartAuctionBean.initialize() : Ready");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Auction addAuction(double startAmount, Date closeTime, Integer itemId) {
        Item item = em.find(Item.class, itemId);
        Auction auction = new Auction();
        auction.setOpenTime(new Date());
        auction.setCloseTime(closeTime);
        auction.setItem(item);
        auction.setStartAmount(startAmount);
        auction.setIncrement(startAmount);
        auction.setStatus("OPEN");
        em.persist(auction);
        em.flush();
        em.find(Auction.class, 1500);
        System.out.println("Auction id: " + auction.getId());
        //int div = 100/0;
        createAuctionTimer(auction.getOpenTime(), auction.getCloseTime(), auction.getId());
        return auction;
    }

    @Override
    public List<Auction> getAuctions() {
        Query query = em.createQuery("SELECT a FROM Auction a");
        try {
            List<Auction> auctions = query.getResultList();
            return auctions;
        } catch (NoResultException nre) {
            return null;
        }
    }

    @RolesAllowed("BIDDER")
    @Lock(WRITE)
    public void addBid(Integer auctionId, double amount, Integer bidderId) {
        User bidder = em.find(User.class, bidderId);
        Auction auction = em.find(Auction.class, auctionId);
        System.out.println(auction);
        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setAuction(auction);
        bid.setBidTime(new Date());
        bid.setBidder(bidder);
        if (amount > auction.getIncrement() && auction.getStatus().equals("OPEN") && auction.getCloseTime().after(new Date())) {
            auction.setIncrement(amount);
            bid.setApproval("Approved");
        } else {
            bid.setApproval("Denied");
        }
        em.persist(bid);
    }

    @Lock(READ)
    public List<Bid> getBids() {
        Query query = em.createQuery("SELECT b FROM Bid b");
        try {
            List<Bid> bidds = query.getResultList();
            return bidds;
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Timeout
    public void timeout(Timer timer) {
    //timer tiene la información de lo que yo programé ahí
        //Lo que vamos a hacer es crear un timer para cerrar esa subasta, irá a la BD y pondrá el CLOSE 
        String inforString = (String) timer.getInfo(); //Puede ser cualquier objecto serializable y castear del otro lado
        System.out.println("StartAuctionBean: timeout ocurred " + inforString);
        String auctionId = inforString.substring(18);
        System.out.println("Close Auction: " + auctionId);
        try {
            closeAuction(new Integer(auctionId));
        } catch (CloseException ex) {
            Logger.getLogger(StartAuctionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createAuctionTimer(Date openTime, Date closeTime, Integer auctionId) {
        long duration = closeTime.getTime() - openTime.getTime();

        System.out.println("CLOSE TIME: " + closeTime);
        System.out.println("OPEN TIME: " + openTime);
        System.out.println("StartAuctionBean.createAuctionTimer " + duration + " AuctionID: " + auctionId);
        timerService.createTimer(1000, "Auction timeout: " + auctionId);
        //TimeService t = sessionContext.getTimerService(); Es otra forma de crear el TimeService        
    }

    public void closeAuction(Integer auctionId) throws CloseException {
        //Sentencias necesarias para ir a la BD y colocarle el CLOSE
        Auction auction = null;
        String status;

        auction = em.find(Auction.class, auctionId);
        System.out.println("Auction find: " + auction.getId());

        if (auction == null) {
            throw new CloseException("Auction # " + auctionId + " is not Open.");
        }
        status = auction.getStatus();
        if (status.compareTo("OPEN") == 0) {
            auction.setStatus("CLOSED");
            auction = em.merge(auction);
        } else {
            throw new CloseException("Auction # " + auctionId + " is not Open.");
        }
    }

    /*@Override
     public void ejbTimeout(Timer timer) {        
     }*/
    @PreDestroy
    public void shutDownTimer() {
        Collection<Timer> timers = timerService.getTimers();
        //De esta forma cada vez que se inicie el server los timers serán borrados, así no tenemos
        System.out.println("shotDownTimer - existing timers: " + timers);
        for (Timer timer : timers) {
            timer.cancel();
            System.out.println("Timer canceled: " + timer);
        }
    }
}
