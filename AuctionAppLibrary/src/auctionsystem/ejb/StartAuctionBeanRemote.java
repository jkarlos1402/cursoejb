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
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Humberto
 */
@Remote
public interface StartAuctionBeanRemote {
   
    public Auction addAuction(double startAmount, Date closeTime, Integer itemId);
    List<Auction> getAuctions();
    void addBid(Integer auctionId, double amount, Integer bidderId);
    List<Bid> getBids();
}
