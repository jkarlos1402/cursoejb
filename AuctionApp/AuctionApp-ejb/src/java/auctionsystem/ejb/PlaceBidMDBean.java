/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.dto.PlaceBidMessage;
import exception.PlaceBidException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Humberto
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "PlaceBidMDB"),
    @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "Approved = 'true'")
})
public class PlaceBidMDBean implements MessageListener {

    @EJB
    private AuctionManagerBeanLocal auctionManagerBean;

    @Resource
    private EJBContext context;

    //DEfinir un messageSelector para que si no se cumple, este bean no se ejecute, no tome el mensaje,
    //si es igual a true se escucha el mensaje , si no  no se escucha

    public PlaceBidMDBean() {
    }

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = null;
        PlaceBidMessage placeBidMessage = null;
        Integer auctionId = null;
        Integer bidderId = null;
        double amount = 0.0;

        try {
            objectMessage = (ObjectMessage) message;
        } catch (ClassCastException cce) {
            throw new EJBException(cce);
        }
        try {
            placeBidMessage = (PlaceBidMessage) objectMessage.getObject();
        } catch (JMSException je) {
            throw new EJBException(je);
        }
        auctionId = placeBidMessage.getAuctionID();
        bidderId = placeBidMessage.getBidderID();
        amount = placeBidMessage.getAmount();
        try {
            auctionManagerBean.placeBid(auctionId, bidderId, amount);
        } catch (PlaceBidException ex) {
            System.out.println("Placed: PlaceBidException "+ex.getMessage());
            context.setRollbackOnly();
        }
        System.out.println("onMessage()");
    }
}
