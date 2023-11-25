package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.broker.MessagePublicationReportBroker;
import com.tbot.ruler.broker.MessagePublishBroker;
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
public class BrokerLifetimeService {

    @Autowired
    private MessagePublishBroker messagePublishBroker;

    @Autowired
    private MessagePublicationReportBroker publicationReportBroker;

    @Autowired
    private ThreadPoolTaskExecutor rulerTaskExecutor;

    @EventListener
    public void initialize(ApplicationReadyEvent event) {
        startTasks();
    }

    public void startTasks() {
        rulerTaskExecutor.execute(messagePublishBroker);
        rulerTaskExecutor.execute(publicationReportBroker);
    }

    public void stopTasks() {
        messagePublishBroker.stop();
        publicationReportBroker.stop();
    }
}
