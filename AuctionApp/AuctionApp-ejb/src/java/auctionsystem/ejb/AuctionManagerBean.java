/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.dto.PlaceBidMessage;
import auctionsystem.entity.Auction;
import auctionsystem.entity.Bid;
import auctionsystem.entity.Item;
import auctionsystem.entity.User;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Humberto
 */
@Stateless
public class AuctionManagerBean implements AuctionManagerBeanLocal, AuctionManagerBeanRemote {

    @EJB
    private TimeBasedAuctionManagerBean timeBasedAuctionManagerBean;
    @PersistenceContext
    private EntityManager em;
    
     @Resource(mappedName = "jms/QueueConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "PlaceBidMDB")
    private Queue bidsPlacedQueue;

    @PostConstruct
    public void initialize() {
        /*System.out.println("AuctionManagerBean.initialize() @PostConstruct");
         items = new LinkedList<>();
         users = new LinkedList<>();
         users.add(new User("Jennifer", "Whalen", "3 South Washington St. Binghamton, N.Y 13903", new CreditCard("1234-5678-9012-3456".toCharArray(), (short) 11, (short) 25, "503".toCharArray(), "Jennifer Whalen"), "JWHALEN", "Jennifer", "Jennifer123"));
         users.add(new User("Karen", "Colmenares", "1100 Van Ness Avenue Fresno, CA 93724-0002", new CreditCard("0987-6543-2198".toCharArray(), (short) 9, (short) 18, "605".toCharArray(), "Karen Colmenares"), "KCOLMENA", "Karen", "Karen123"));
         */
    }

    @PrePassivate
    private void passivate() {
        System.out.println("AuctionManagerBean.passivate() @PrePassivate");
    }

    @PostActivate
    private void activate() {
        System.out.println("AuctionManagerBean.activate() @PostActivate");
    }

    @Override
    public String communicationTest(String message) {
        System.out.println("AuctionManagerBean.communicationTest: " + message);
        return timeBasedAuctionManagerBean.communicationTest(message);
    }

    @Override
    public Item addItem(String description, String image, Integer sellerId) {
        User user = em.find(User.class, sellerId);
        Item item = new Item();
        item.setDescription(description);
        item.setImage(image);
        item.setSeller(user);
        System.out.println("addItem(" + description + ", " + image + ")");
        em.persist(item);
        em.flush();
        return item;
    }

    @Override
    public List<Item> getItems(Integer sellerId) {
        Query query = em.createQuery("SELECT i FROM Item i WHERE i.seller.id = :sellerId");
        query.setParameter("sellerId", sellerId);
        try {
            List<Item> items = query.getResultList();
            return items;
        } catch (NoResultException nre) {
            return null;
        }
    }

    @PreDestroy
    private void destroy() {
        System.out.println("AuctionManagerBean.destroy() @PreDestroy");
    }

    @Remove
    public void remove() {
        System.out.println("AuctionManagerBean.remove() @Remove");
    }

    @Asynchronous
    public Future<String> checkout() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                System.out.println("Proceso continua!!");
                //throw new RuntimeException();
            } catch (InterruptedException ex) {
                Logger.getLogger(StartAuctionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String orderId = "100";
        return new AsyncResult<>(orderId);
    }

    @Override
    public User login(String displayName, String password) {
        Query query = em.createQuery("SELECT u FROM User u WHERE u.displayName = :displayName");
        query.setParameter("displayName", displayName);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public void placeBid(Integer auctionId, Integer bidderId, Double amount) {
        User bidder = em.find(User.class, bidderId);
        Auction auction = em.find(Auction.class, auctionId);
        System.out.println(auction);
        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setAuction(auction);
        bid.setBidTime(new Date());
        bid.setBidder(bidder);
        if (amount > auction.getIncrement() && auction.getStatus().equals("open") && auction.getCloseTime().after(new Date())) {
            auction.setIncrement(amount);
            bid.setApproval("Approved");
        } else {
            bid.setApproval("Denied");            
        }
        em.persist(bid);
        System.out.println("placeBid");
    }
    
    private void sendBidMessage(Integer auctionId, Integer bidderId, Double amount){
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true,Session.SESSION_TRANSACTED);
            MessageProducer producer = session.createProducer(bidsPlacedQueue);
            PlaceBidMessage placeBidMessageDto = new PlaceBidMessage(auctionId, bidderId, amount);
            ObjectMessage message = session.createObjectMessage(placeBidMessageDto);
            producer.send(message);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally{
            try {
                connection.close();
            } catch (JMSException ex) {
                Logger.getLogger(AuctionManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void addBid(Integer auctionId, double amount, Integer bidderId) {
        sendBidMessage(auctionId, bidderId, amount);
    }
}
