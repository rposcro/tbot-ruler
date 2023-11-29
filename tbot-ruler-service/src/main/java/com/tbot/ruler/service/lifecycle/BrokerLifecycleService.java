package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.broker.MessagePublicationReportBroker;
import com.tbot.ruler.broker.MessagePublishBroker;
import com.tbot.ruler.broker.MessageQueueComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope("singleton")
public class BrokerLifecycleService {

    @Autowired
    private MessagePublishBroker messagePublishBroker;

    @Autowired
    private MessagePublicationReportBroker publicationReportBroker;

    @Autowired
    private MessageQueueComponent messageQueue;

    @Autowired
    private ThreadPoolTaskExecutor rulerTaskExecutor;

    @EventListener
    public void initialize(ApplicationReadyEvent event) {
        startBroker();
    }

    public void startBroker() {
        messageQueue.emptyQueues();
        rulerTaskExecutor.execute(messagePublishBroker);
        rulerTaskExecutor.execute(publicationReportBroker);
    }

    public void stopBroker() {
        messagePublishBroker.stop();
        publicationReportBroker.stop();
    }
}
