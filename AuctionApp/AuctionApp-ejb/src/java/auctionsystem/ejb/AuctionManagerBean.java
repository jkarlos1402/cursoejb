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
import auctionsystem.interceptor.AuctionInterceptor;
import java.util.Date;
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
import javax.ejb.EJBException;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TimerService;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
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
@Interceptors(AuctionInterceptor.class)
public class AuctionManagerBean implements AuctionManagerBeanLocal, AuctionManagerBeanRemote {

    @EJB
    private TimeBasedAuctionManagerBean timeBasedAuctionManagerBean;
    @PersistenceContext
    private EntityManager em;
    @Resource
    private TimerService timerService;
    //JMS
    @Resource(mappedName = "PlaceBidMDB")
    private Queue bidsPlacedQueue;
    @Resource(mappedName = "jms/QueueConnectionFactory")
    private ConnectionFactory connectionFactory;
   
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
    public void addBid(Integer auctionId, double amount, Integer bidderId) {
        sendBidStatusUpdateMessage(auctionId, bidderId, amount);
    }

    @Override
    public void placeBid(Integer auctionId, Integer bidderId, Double amount) {
        System.out.println("placeBid()");
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

    private void sendBidStatusUpdateMessage(Integer auctionID, Integer bidderID, double amount) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            MessageProducer producer = session.createProducer(bidsPlacedQueue);
            PlaceBidMessage PlaceBidMessageDTO = new PlaceBidMessage(auctionID, bidderID, amount);

            ObjectMessage message = session.createObjectMessage(PlaceBidMessageDTO);
            //int property = auctionID.intValue();
            //Si ponemos false, y declaramos un pid nadie tomará ese mensaje, se queda en la cola hasta que alguien lo consuma
            message.setStringProperty("Approved", "true");
            producer.send(message, DeliveryMode.NON_PERSISTENT, 1, 30000);
        } catch (JMSException je) {
            throw new EJBException(je);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException je) {
                    throw new EJBException(je);
                } finally {
                    connection = null;
                }
            }
        }
    }
}
