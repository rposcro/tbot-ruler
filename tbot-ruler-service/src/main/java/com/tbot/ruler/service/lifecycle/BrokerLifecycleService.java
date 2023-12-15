package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.broker.MessagePublicationReportBroker;
import com.tbot.ruler.broker.MessagePublishBroker;
import com.tbot.ruler.broker.MessageQueueComponent;
import com.tbot.ruler.jobs.JobRunner;
import com.tbot.ruler.jobs.JobTrigger;
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

    private JobRunner messageBrokerJobRunner;
    private JobRunner reportBrokerJobRunner;

    @EventListener
    public void initialize(ApplicationReadyEvent event) {
        this.messageBrokerJobRunner = new JobRunner(messagePublishBroker, JobTrigger.instantTrigger());
        this.reportBrokerJobRunner = new JobRunner(publicationReportBroker, JobTrigger.instantTrigger());
        startBroker();
    }

    public void startBroker() {
        messageQueue.emptyQueues();
        rulerTaskExecutor.execute(messageBrokerJobRunner);
        rulerTaskExecutor.execute(reportBrokerJobRunner);
    }

    public void stopBroker() {
        messageBrokerJobRunner.stop();
        reportBrokerJobRunner.stop();
    }
}
