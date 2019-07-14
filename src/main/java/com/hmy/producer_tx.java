package com.hmy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.util.Random;

public class producer_tx {

    public static final String ACTIVE_BROKER_BIND_URL = "tcp://192.168.226.131:61616";
    public static final String QUEUE_NAME = "queue1";

    public static void main(String[] args) throws JMSException {
        //创建工厂
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_BROKER_BIND_URL);
        //创建连接
        Connection connection = factory.createConnection();
        connection.start();
        //创建session,true开启事务
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

        //创建destination：queue or topic
        Queue queue = session.createQueue(QUEUE_NAME);
        //创建生产者
        MessageProducer producer = session.createProducer(queue);

        for (int i = 0; i < 7; i++) {

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
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSPriority(Message.DEFAULT_PRIORITY);
            message.setJMSMessageID("123456");
            message.setJMSExpiration(5000L);

            /**
             * 消息属性，增加消息的过滤，去重等特性
             */
            message.setStringProperty("k1", "v1");
            //发送消息
            producer.send(message);
        }

        //事务提交
        session.commit();
        //关闭资源
        producer.close();
        session.close();
        connection.close();

        System.out.println("消息发送成功");

    }
}
