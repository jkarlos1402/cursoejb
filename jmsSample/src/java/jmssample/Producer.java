/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmssample;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author Curso
 */
public class Producer {

    @Resource(mappedName = "jms/QueueConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "HelloQueue")
    private static Destination helloQueue;
    
    public static void main(String[] args) {
        System.out.println("Iniciando...");
        sendJMSMessage("Hello consumer...");
        System.out.println("Terminando");
    }

    private static void sendJMSMessage(String message) {
        Connection connection = null;
        Session session = null;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(helloQueue);
            
            TextMessage msg = session.createTextMessage();
            msg.setText(message);
            
            producer.send(msg);
            System.out.println("Producer.sendJMSMessage: "+message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(session != null){
                try {
                    session.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }                
            }
            if(connection != null){
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }                
            }
        }
    }
}
