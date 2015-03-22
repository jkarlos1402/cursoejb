/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.entity.User;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author OCE
 */
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateful
public class UserAuctionBean implements UserAuctionBeanRemote, SessionSynchronization {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void afterBegin() throws EJBException, RemoteException {
        System.out.println("afterBegin");
    }

    @Override
    public void beforeCompletion() throws EJBException, RemoteException {
        System.out.println("beforeCompletion");
    }

    @Override
    public void afterCompletion(boolean committed) throws EJBException, RemoteException {
        System.out.println("afterCompletion " + committed);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addUser() {
        User user = new User();
        user.setFirtName("Juan Carlos");
        user.setLastName("Piña");
        user.setDisplayName("jkarlos");
        user.setPassword("123");
        
        em.persist(user);
    }

}
