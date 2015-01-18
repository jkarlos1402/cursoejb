/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package auctionsystem.ejb;

import auctionsystem.entity.Item;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Curso
 */
@Remote
public interface AuctionManagerBeanRemote {

    String communicationTest(String message);

    Item addItem(String description, String image);

    List<Item> getItems();
    
}
