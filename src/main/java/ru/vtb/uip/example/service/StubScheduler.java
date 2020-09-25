package ru.vtb.uip.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.concurrent.ExecutionException;

@EnableScheduling
@Component
public class StubScheduler {

    @Autowired
    private JmsService jmsService;

    @Scheduled(fixedDelay=Long.MAX_VALUE)
    private void taskRunOnce() throws ExecutionException, InterruptedException, JMSException {
        Message receivedMessage = jmsService.waitForFirstSelectedMessage();
        if (receivedMessage instanceof TextMessage)
            System.out.println(((TextMessage) receivedMessage).getText());
        else
            throw new JMSException("Invalid Message format");
    }

}
