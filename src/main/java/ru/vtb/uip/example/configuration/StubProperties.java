package ru.vtb.uip.example.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
@Configuration
@ConfigurationProperties(prefix = "stub")
public class StubProperties {
    public void setQueues(List<String> queues) {

        this.queues = queues;
    }

    public List<String> getQueues() {
        return queues;
    }

    private List<String> queues = new ArrayList<>();
}
