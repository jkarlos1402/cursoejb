
package auctionsystem.ejb;

import auctionsystem.entity.Auction;
import auctionsystem.entity.Item;
import auctionsystem.entity.User;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author Curso
 */
@Singleton
public class StartAuctionBean implements StartAuctionBeanRemote{
    
    List<Auction> auctions;

    @PostConstruct
    private void inicialize(){
        auctions = new LinkedList<>();
    }
    
    
    @Override
    public Auction addAuction(double startAmount, Date closeTime, Item item, User seller) {
        System.out.println("paso aqui");
        Auction auction = new Auction(startAmount, "OPEN", new Date(), closeTime, item, seller);
        auctions.add(auction);
        System.out.println(auction);
        return auction;
    }

    @Override
    public List<Auction> getAuction() {
        return auctions;
    }
    
}
