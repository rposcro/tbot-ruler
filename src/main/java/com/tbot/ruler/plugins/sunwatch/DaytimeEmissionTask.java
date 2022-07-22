package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePublisher;
import com.tbot.ruler.things.EmitterId;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
@Builder
public class DaytimeEmissionTask implements Runnable {

    @NonNull private Message sunriseMessage;
    @NonNull private Message sunsetMessage;
    @NonNull private DaytimeEmissionTrigger daytimeTrigger;
    @NonNull private MessagePublisher messagePublisher;
    @NonNull private EmitterId emitterId;

    @Builder.Default
    private final Semaphore emissionLock = new Semaphore(1);

    public void run() {
        if (emissionLock.tryAcquire()) {
            boolean isSunrise = daytimeTrigger.triggeredOnSunrise();
            log.info("[EMISSION] Daytime event for emitter {}, event {}", emitterId.getValue(), isSunrise ? "Sunrise" : "Sunset");
            messagePublisher.acceptMessage(isSunrise ? sunriseMessage : sunsetMessage);
            emissionLock.release();
        }
    }

    public void acceptDeliveryReport(DeliveryReport deliveryReport) {

    }
}
