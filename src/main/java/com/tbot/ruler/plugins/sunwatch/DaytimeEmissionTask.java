package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.EmitterId;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Builder
public class DaytimeEmissionTask implements Runnable {

    @NonNull private Message sunriseMessage;
    @NonNull private Message sunsetMessage;
    @NonNull private DaytimeEmissionTrigger daytimeTrigger;
    @NonNull private Consumer<Message> messageConsumer;
    @NonNull private EmitterId emitterId;

    public void run() {
        boolean isSunrise = daytimeTrigger.triggeredOnSunrise();
        log.info("[EMISSION] Daytime event for emitter {}, event {}", emitterId.getValue(), isSunrise ? "Sunrise" : "Sunset");
        messageConsumer.accept(isSunrise ? sunriseMessage : sunsetMessage);
    }
}
