package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.broker.MessagePublicationReportBroker;
import com.tbot.ruler.broker.MessagePublishBroker;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.jobs.JobRunner;
import com.tbot.ruler.jobs.JobTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
    private ThreadPoolTaskExecutor rulerTaskExecutor;

    private JobRunner messageBrokerJobRunner;
    private JobRunner reportBrokerJobRunner;

    public void startBroker() {
        if (messageBrokerJobRunner == null) {
            messageBrokerJobRunner = new JobRunner(messagePublishBroker, JobTrigger.instantTrigger());
            reportBrokerJobRunner = new JobRunner(publicationReportBroker, JobTrigger.instantTrigger());
        } else if (messageBrokerJobRunner.isRunning() || reportBrokerJobRunner.isRunning()) {
            throw new LifecycleException("Message broker is running!");
        }

        rulerTaskExecutor.execute(messageBrokerJobRunner);
        rulerTaskExecutor.execute(reportBrokerJobRunner);
    }

    public void stopBroker() {
        messageBrokerJobRunner.stop();
        reportBrokerJobRunner.stop();
    }
}
