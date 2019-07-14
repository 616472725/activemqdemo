package com.hmy;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

public class Consumer {
//    public static final String ACTIVE_BROKER_BIND_URL = "tcp://192.168.226.131:61616";
    public static final String ACTIVE_BROKER_BIND_URL = "tcp://localhost:61616";
    public static final String QUEUE_NAME = "queue1";

    public static void main(String[] args) throws JMSException, IOException {

        System.out.println("");

        //创建工厂
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_BROKER_BIND_URL);
        //创建连接
        Connection connection = factory.createConnection();
        connection.start();
        //创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建destination：queue or topic
        Queue queue = session.createQueue(QUEUE_NAME);

        //创建消费者
        MessageConsumer consumer = session.createConsumer(queue);
        /*while (true) {
            //两种方式，一个是有timeout的，表示阻塞的时间，-1表示一直阻塞
            TextMessage receive = (TextMessage) consumer.receive(5000L);
//            TextMessage receive = (TextMessage) consumer.receive();
            if (null != receive) {
                System.out.println("收到消息：" + receive.getText());
            } else {
                break;
            }
        }*/

        //使用监听器
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                if (textMessage != null) {
                    try {
                        System.out.println(textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        System.in.read();
        consumer.close();
        session.close();
        connection.close();

    }
}
