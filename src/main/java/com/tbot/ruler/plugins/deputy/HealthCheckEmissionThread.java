package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestResponse;
import com.tbot.ruler.signals.BooleanSignalValue;
import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.EmitterId;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@Builder
class HealthCheckEmissionThread implements EmissionThread, Runnable {

    private EmitterId emitterId;
    private Consumer<EmitterSignal> signalConsumer;
    private RestGetCommand healthCheckCommand;

    @Override
    public Runnable getRunnable() {
        return this;
    }

    @Override
    public void run() {
        try {
            log.info("[EMISSION] Deputy health check for emitter {}", emitterId.getValue());
            RestResponse response = healthCheckCommand.sendGet();
            if (response.getStatusCode() == 200) {
                signalConsumer.accept(new EmitterSignal(new BooleanSignalValue(true), emitterId));
            }
        }
        catch(Exception e) {
            log.info("Deputy health check failed: " + e.getMessage());
            signalConsumer.accept(new EmitterSignal(new BooleanSignalValue(false), emitterId));
        }
    }
}