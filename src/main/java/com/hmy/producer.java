package com.hmy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.AsyncCallback;
import org.apache.activemq.command.*;

import javax.jms.*;
import javax.jms.Message;
import java.util.Random;
import java.util.UUID;

public class producer {

//    public static final String ACTIVE_BROKER_BIND_URL = "tcp://localhost:61616";
//    public static final String ACTIVE_BROKER_BIND_URL = "tcp://192.168.226.131:61616";
//    public static final String ACTIVE_BROKER_BIND_URL = "nio://192.168.226.131:61618";
//    public static final String ACTIVE_BROKER_BIND_URL = "nio://192.168.226.131:61608";
    public static final String ACTIVE_BROKER_BIND_URL = "failover:(tcp://zk1:61616,tcp://zk2:61616,tcp://zk3:61616)";
    public static final String QUEUE_NAME = "queue1";

    public static void main(String[] args) throws JMSException {
        //创建工厂
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_BROKER_BIND_URL);
        factory.setSendAcksAsync(true);//使用异步投递
        //创建连接
        Connection connection = factory.createConnection();
        connection.start();
        //创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建destination：queue or topic
        Queue queue = session.createQueue(QUEUE_NAME);
        //创建生产者
        ActiveMQMessageProducer producer = (ActiveMQMessageProducer) session.createProducer(queue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        for (int i = 0; i < 10000; i++) {

            /**
             * 常用消息体
             * 字符串
             * MAP
             * stream流
             * byte 字节
             * object 对象
             *
             * Message message = new ActiveMQMapMessage();
             *             message = new ActiveMQBytesMessage();
             *             message = new ActiveMQStreamMessage();
             *             message = new ActiveMQObjectMessage();
             *             message = new ActiveMQTextMessage();
             */
            //创建消息
            Message message = new ActiveMQTextMessage();


            ((ActiveMQTextMessage) message).setText("hello" + new Random(10).nextInt());
            //消息属性介绍，消息头
            /**
             * DeliveryMode:是否持久化 1是非持久化，2是持久化
             * Priority：优先级，默认是4
             * MessageId：消息ID，可用于保证消息不重复消费
             * Expiration：消息过期时间
             */
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);//这里设置是不起作用的
            message.setJMSPriority(Message.DEFAULT_PRIORITY);
            message.setJMSMessageID("123456");
            message.setJMSExpiration(5000L);

            /**
             * 消息属性，增加消息的过滤，去重等特性
             */
            message.setStringProperty("k1", "v1");
            //发送消息
//            producer.send(message, DeliveryMode.NON_PERSISTENT, 4, 0);
//            producer.send(message);

            message.setJMSMessageID(new Random().nextInt() + "");
            producer.send(message, new AsyncCallback() {
                @Override
                public void onSuccess() {
                    try {
                        System.out.println(message.getJMSMessageID() + "发送成功");
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onException(JMSException exception) {
                    try {
                        System.out.println(message.getJMSMessageID() + "发送失败");
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //关闭资源
        producer.close();
        session.close();
        connection.close();

        System.out.println("消息发送成功");

    }
}
