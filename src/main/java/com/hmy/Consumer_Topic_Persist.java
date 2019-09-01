package com.hmy;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

public class Consumer_Topic_Persist {
    public static final String ACTIVE_BROKER_BIND_URL = "tcp://192.168.226.131:61616";
    public static final String QUEUE_NAME = "topic1_persist";

    public static void main(String[] args) throws JMSException, IOException {

        //创建工厂
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_BROKER_BIND_URL);
        //创建连接
        Connection connection = factory.createConnection();
        connection.setClientID("li4");
//        connection.start();
        //创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建destination：queue or topic
        Topic topic = session.createTopic(QUEUE_NAME);
        TopicSubscriber subscriber = session.createDurableSubscriber(topic, "remark");
        connection.start();
        Message message = subscriber.receive();
        while (message != null) {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("收到持久化消息：" + textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
            message = subscriber.receive();
        }

        System.in.read();
        subscriber.close();
        session.close();
        connection.close();

    }
}
