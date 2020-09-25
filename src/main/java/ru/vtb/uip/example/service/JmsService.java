package ru.vtb.uip.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import ru.vtb.uip.example.configuration.StubProperties;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@EnableAsync
public class JmsService {

    @Autowired
    private StubProperties properties;

    private static final String CORRELID_SELECTOR = "JMSCorrelationID='ID:000000000000000000000000000000000000000000000777'";
    private static final long TIMEOUT = 30000L;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Async("asyncTaskExecutor")
    protected CompletableFuture<Message> asyncRequestFromQueue(String destinationName, String selector) {
        Message receivedMessage = jmsTemplate.receiveSelected(destinationName, selector);
        return CompletableFuture.completedFuture(receivedMessage);
    }

    public Message waitForFirstSelectedMessage() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Message>> completableFutures = new ArrayList<>();
        Message firstDoneMessage = null;
        properties.getQueues().forEach(queue ->
            completableFutures.add(asyncRequestFromQueue(queue, CORRELID_SELECTOR))
        );
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < TIMEOUT) {
            for(CompletableFuture<Message> element: completableFutures) {
                if (element.isDone()) {
                    firstDoneMessage = element.get();
                    break;
                }
            }
        }
        return firstDoneMessage;
    }

}
