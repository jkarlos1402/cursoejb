/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionappappclient;

import auctionsystem.ejb.AuctionManagerBeanRemote;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Humberto
 */
public class Main {

    @EJB
    private static AuctionManagerBeanRemote auctionManagerBean;
    /**
     * @param args the command line arguments
     */
    private static AuctionManagerBeanRemote auctionManagerBeanRemote;

    public static void main(String[] args) {

        String jndiPath = "java:global/AuctionApp/AuctionApp-ejb/AuctionManagerBean!auctionsystem.ejb.AuctionManagerBeanRemote";
        try {
            //System.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
            //System.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            //System.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
            //System.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
            //System.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
            Context ctx = new InitialContext();
            System.out.println("JNDI look up mechanism:");
            System.out.println("auctionappappclient.Main.main: looking up bean at: "
                    + jndiPath);
            auctionManagerBeanRemote = (AuctionManagerBeanRemote) ctx.lookup(jndiPath);
            System.out.println("auctionappappclient.Main.main: found AuctionManagerBeanRemote: "
                    + auctionManagerBeanRemote);
            System.out.println("auctionappappclient.Main.main: calling communicationTest");
            String greeting = auctionManagerBeanRemote.communicationTest("hello on startup");
            System.out.println("auctionappappclient.Main.main: AuctionManagerBean said: "
                    + greeting);

            System.out.println("\nEJB injected automatically:");
            String message = "hello";
            System.out.println("Sending: " + message);
            String reply = auctionManagerBean.communicationTest(message);
            System.out.println(reply);
        } catch (NamingException ex) {
            System.err.println("auctionappappclient.Main.main: Could not find AuctionManagerBeanRemote");
            System.err.println("auctionappappclient.Main.main: JNDI path used for lookup: " + jndiPath);
            ex.printStackTrace();
        }
    }
}
