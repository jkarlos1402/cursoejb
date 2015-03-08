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
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author Curso
 */
public class Consumer {
    @Resource(mappedName = "jms/QueueConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "HelloQueue")
    private static Destination helloQueue;
    
    public static void main(String[] args) {
        consumeJMSMessage();
    }
    private static void consumeJMSMessage() {
        Connection connection = null;
        Session session = null;

        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(helloQueue);
            connection.start();
            
            Message message = consumer.receive();
            
            if( message instanceof TextMessage){
                String messageValue = ((TextMessage)message).getText();
                System.out.println("Consumer.consumeJMSMessage: "+messageValue);
            }else{
                System.out.println("Consumer.consumeJMSMessage: "+message);
            }
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
