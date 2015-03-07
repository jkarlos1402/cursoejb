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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import static javax.ejb.ConcurrencyManagementType.CONTAINER;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import static javax.ejb.LockType.WRITE;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Humberto
 */
@ConcurrencyManagement(CONTAINER)
@Startup
@Singleton
public class StartAuctionBean implements StartAuctionBeanRemote {

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void initialize() {
        System.out.println("StartAuctionBean.initialize() : Ready");
    }

    @Override
    public Auction addAuction(double startAmount, Date closeTime, Integer itemId) {
        Item item = em.find(Item.class, itemId);
        Auction auction = new Auction();
        auction.setOpenTime(new Date());
        auction.setCloseTime(closeTime);
        auction.setItem(item);
        auction.setStartAmount(startAmount);
        auction.setIncrement(startAmount);
        auction.setStatus("open");
        em.persist(auction);
        em.flush();
        System.out.println("Auction id: " + auction.getId());
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
}
