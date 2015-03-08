package auctionsystem.ejb;

import auctionsystem.dto.PlaceBidMessage;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Curso
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "PlaceBidMDB")
})
public class PlaceBidMDBean implements MessageListener {
    @EJB
    private AuctionManagerBeanLocal auctionManagerBean;

    public PlaceBidMDBean() {
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("onMessage");
        ObjectMessage objectMessage = null;
        PlaceBidMessage placeBidMessage = null;
        Integer auctionId = 0;
        Integer bidderId = 0;
        Double amount = 0.0;
        
        try {
            objectMessage = (ObjectMessage)message;
        } catch (ClassCastException cce) {
            System.out.println(cce.getMessage());
        }
        try {
            placeBidMessage = (PlaceBidMessage)objectMessage.getObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        auctionId = placeBidMessage.getAuctionId();
        bidderId = placeBidMessage.getBidderId();
        amount = placeBidMessage.getAmount();
        
        auctionManagerBean.placeBid(auctionId, bidderId, amount);
    }

}
