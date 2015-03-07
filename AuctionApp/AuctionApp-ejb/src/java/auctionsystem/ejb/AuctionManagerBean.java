/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.entity.Item;
import auctionsystem.entity.User;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Humberto
 */
//@Stateful
public class AuctionManagerBean implements AuctionManagerBeanLocal, AuctionManagerBeanRemote {

    @EJB
    private TimeBasedAuctionManagerBean timeBasedAuctionManagerBean;
    @PersistenceContext
    private EntityManager em;

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
}
