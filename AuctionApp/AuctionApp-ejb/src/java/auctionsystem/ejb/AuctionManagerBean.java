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
public class AuctionManagerBean implements AuctionManagerBeanRemote {

    public String communicationTest(String message) {
        return message;
    }

}
