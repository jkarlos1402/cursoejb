/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionsystem.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author Humberto
 */
@Startup
@DependsOn({"B", "CFirst"})
@Singleton
@LocalBean
public class A {

    @PostConstruct
    public void init(){
        System.out.println("A.init() : @PostConstruct");
    }
}
