/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionappappclient;

import auctionsystem.ejb.AuctionManagerBeanRemote;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Curso
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static AuctionManagerBeanRemote auctionManagerBeanRemote;

    public static void main(String[] args) throws NamingException {
        System.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        System.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprice");
        System.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        System.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        System.setProperty("org.omg.CORBA.ORBInitialPort", "3700"); //puerto iiop
        Context context = new InitialContext();
        String jndiPath = "java:global/AuctionApp/AuctionApp-ejb/AuctionManagerBean!auctionsystem.ejb.AuctionManagerBeanRemote";
        auctionManagerBeanRemote = (AuctionManagerBeanRemote) context.lookup(jndiPath);
        System.out.println("Hola mundo desde EJB "+auctionManagerBeanRemote);
        
        String greeting = auctionManagerBeanRemote.communicationTest("Hello EJB start up");
        
        System.out.println("AuctionManagerBean said: "+greeting);
    }

}
