/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import exception.PlaceBidException;
import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author Humberto
 */
@Local
public interface AuctionManagerBeanLocal {

    void placeBid(Integer auctionId, Integer bidderId, Double amount) throws PlaceBidException;
}
