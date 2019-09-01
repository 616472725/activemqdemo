package com.hmy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.util.Random;

public class producer_topic_persist {

    public static final String ACTIVE_BROKER_BIND_URL = "tcp://192.168.226.131:61616";
    public static final String QUEUE_NAME = "topic1_persist";

    public static void main(String[] args) throws JMSException {
        //创建工厂
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_BROKER_BIND_URL);
        //创建连接
        Connection connection = factory.createConnection();

        //创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建destination：queue or topic
        Topic topic = session.createTopic(QUEUE_NAME);
        //创建生产者
        MessageProducer producer = session.createProducer(topic);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        connection.start();

        for (int i = 0; i < 3; i++) {

            //创建消息
            Message message = new ActiveMQTextMessage();
            ((ActiveMQTextMessage) message).setText("hello" + new Random(10).nextInt());
            //发送消息
            producer.send(message);
        }

        //关闭资源
        producer.close();
        session.close();
        connection.close();

        System.out.println("消息发送成功");

    }
}
