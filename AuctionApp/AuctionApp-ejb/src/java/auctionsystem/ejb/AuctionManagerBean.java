/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import javax.ejb.Stateless;

/**
 *
 * @author Curso
 */
@Stateless
public class AuctionManagerBean implements AuctionManagerBeanLocal {

    @Override
    public String comunicationTest(String message) {
        return message + "!!!!!!";
    }
    
}
