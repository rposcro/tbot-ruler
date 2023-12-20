package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
public class DaytimeEmissionJob implements Job {

    private final Message dayTimeMessage;
    private final Message nightTimeMessage;
    private final SunCalculator sunCalculator;
    private final MessagePublisher messagePublisher;
    private final String actuatorUuid;

    private final String jobName;
    private final Semaphore emissionLock;

    @Builder
    public DaytimeEmissionJob(
            @NonNull Message dayTimeMessage,
            @NonNull Message nightTimeMessage,
            @NonNull SunCalculator sunCalculator,
            @NonNull MessagePublisher messagePublisher,
            @NonNull String actuatorUuid) {
        this.dayTimeMessage = dayTimeMessage;
        this.nightTimeMessage = nightTimeMessage;
        this.sunCalculator = sunCalculator;
        this.messagePublisher = messagePublisher;
        this.actuatorUuid = actuatorUuid;
        this.jobName = "SunWatch-Daytime-Job@" + actuatorUuid;
        this.emissionLock = new Semaphore(1);
    }

    @Override
    public void doJob() {
        if (emissionLock.tryAcquire()) {
            boolean isDaytime = sunCalculator.isDaytimeNow();
            log.info("[EMISSION] Daytime event for actuator {}, event for {}", actuatorUuid, isDaytime ? "DayTime" : "NightTime");
            messagePublisher.publishMessage(isDaytime ? dayTimeMessage : nightTimeMessage);
            emissionLock.release();
        }
    }

    @Override
    public String getName() {
        return jobName;
    }
}
