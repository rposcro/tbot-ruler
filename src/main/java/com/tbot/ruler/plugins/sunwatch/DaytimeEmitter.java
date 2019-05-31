package com.tbot.ruler.plugins.sunwatch;

import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Builder
@AllArgsConstructor
@Getter
public class DaytimeEmitter implements Emitter, Runnable {

    private EmitterId id;
    private EmitterMetadata metadata;
    private EmitterSignal sunriseSignal;
    private EmitterSignal sunsetSignal;
    private DaytimeTrigger daytimeTrigger;
    private Consumer<EmitterSignal> signalConsumer;

    @Override
    public void run() {
        boolean isSunrise = daytimeTrigger.triggeredOnSunrise();
        log.info("[EMISSION] Daytime event for emitter {}, event {}", id.getValue(), isSunrise ? "Sunrise" : "Sunset");
        signalConsumer.accept(isSunrise ? sunriseSignal : sunsetSignal);
    }
}
