package com.ibm.eventautomation.demos.acme.utils;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;

import org.apache.kafka.connect.data.Struct;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.WMQConstants;

public class SendToMQ 
{
    private static MQConnectionFactory mqConnFactory;
    private static JMSContext jmsCtxt;
    private static JMSProducer jmsProducer;
    private static MQQueue queue;
    private static boolean connected=false;
    private static String lock="lock";
    
    private static void connectToMQ()
    {
        if (!connected)
        {
            try
            {
                mqConnFactory = new MQConnectionFactory();
                mqConnFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
                mqConnFactory.setQueueManager("orders");
                mqConnFactory.setChannel("SYSTEM.DEF.SVRCONN");
                mqConnFactory.setConnectionNameList("orders-ibm-mq(1414)");
                mqConnFactory.setAppName("OrderSystem");
                queue = new MQQueue("PAYMENT.REQ");
                queue.setMessageBodyStyle(WMQConstants.WMQ_MESSAGE_BODY_MQ);
                jmsCtxt = mqConnFactory.createContext(JMSContext.SESSION_TRANSACTED);
                jmsProducer = jmsCtxt.createProducer();
                connected=true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public static void send(Struct struct) 
    {
        String data = "{\"id\":\""+struct.getString("id")+"\",\"customer\":\""+struct.getString("customer")+"\",\"customerid\":\""+struct.getString("customerid")+"\",\"description\":\""+struct.getString("description")+"\",\"price\":"+struct.getFloat64("price")+",\"quantity\":"+struct.getInt32("quantity")+",\"region\":\""+struct.getString("region")+"\",\"ordertime\":\""+struct.getString("timestamp")+"\"}";
        System.out.println("data="+data);
        try 
        {
            synchronized(lock)
            {
                connectToMQ();
                jmsProducer.send(queue, data);
                jmsCtxt.commit();
            }
        } 
        catch (Exception jmsex) 
        {
            jmsex.printStackTrace();
        }
    }
    
}
