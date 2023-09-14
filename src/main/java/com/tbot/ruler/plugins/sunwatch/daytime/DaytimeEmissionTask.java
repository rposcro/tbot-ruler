package com.tbot.ruler.plugins.sunwatch.daytime;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.plugins.sunwatch.SunCalculator;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
public class DaytimeEmissionTask implements Runnable {

    private Message dayTimeMessage;
    private Message nightTimeMessage;
    private SunCalculator sunCalculator;
    private MessagePublisher messagePublisher;
    private String emitterId;

    private final Semaphore emissionLock = new Semaphore(1);

    @Builder
    public DaytimeEmissionTask(
            @NonNull Message dayTimeMessage,
            @NonNull Message nightTimeMessage,
            @NonNull SunCalculator sunCalculator,
            @NonNull MessagePublisher messagePublisher,
            @NonNull String emitterId) {
        this.dayTimeMessage = dayTimeMessage;
        this.nightTimeMessage = nightTimeMessage;
        this.sunCalculator = sunCalculator;
        this.messagePublisher = messagePublisher;
        this.emitterId = emitterId;
    }

    public void run() {
        if (emissionLock.tryAcquire()) {
            boolean isDaytime = sunCalculator.isDaytimeNow();
            log.info("[EMISSION] Daytime event for emitter {}, event for {}", emitterId, isDaytime ? "DayTime" : "NightTime");
            messagePublisher.publishMessage(isDaytime ? dayTimeMessage : nightTimeMessage);
            emissionLock.release();
        }
    }
}
