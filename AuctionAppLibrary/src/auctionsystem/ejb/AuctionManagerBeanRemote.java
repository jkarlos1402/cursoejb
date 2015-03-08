/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import auctionsystem.entity.Item;
import auctionsystem.entity.User;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.Remote;

/**
 *
 * @author Humberto
 */
@Remote
public interface AuctionManagerBeanRemote {

    public String communicationTest(String message);

    public Item addItem(String description, String image, Integer sellerId);

    public List<Item> getItems(Integer sellerId);

    public void remove();

    public Future<String> checkout();

    User login(String displayName, String password);

    public void addBid(Integer auctionId, double amount, Integer bidderId);
}
