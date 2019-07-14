package com.hmy.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Service
public class SpringConsumer {

    @Autowired
    JmsTemplate jmsTemplate;

    public static void main(String[] args) throws JMSException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:application.xml");

        SpringConsumer springConsumer = (SpringConsumer) applicationContext.getBean("springConsumer");
        String val = (String) springConsumer.jmsTemplate.receiveAndConvert();
        System.out.println("收到消息：" + val);
    }
}
