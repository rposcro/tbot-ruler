package com.tbot.ruler.broker;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.exceptions.SignalException;
import com.tbot.ruler.service.AppliancesAgentService;
import com.tbot.ruler.service.things.ApplianceBindingsService;
import com.tbot.ruler.service.AppliancesService;
import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.things.EmitterId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class SignalEmissionBroker {

    @Autowired
    private AppliancesService appliancesService;

    @Autowired
    private ApplianceBindingsService applianceBindingsService;

    @Autowired
    private AppliancesAgentService appliancesAgentService;

    private LinkedBlockingQueue<EmitterSignal> signalsQueue;

    @Autowired
    public SignalEmissionBroker(@Value("${ruler.broker.emissionQueueLength:50}") int queueSize) {
        log.info("Emission queue length set to {}", queueSize);
        signalsQueue = new LinkedBlockingQueue<>(queueSize);
    }

    public void receiveSignalFromEmitter(EmitterSignal signal) {
        signalsQueue.add(signal);
        log.debug("Queued signal {} ", signal);
    }

    public Runnable brokerRunnable() {
        return () -> {
            while(true) {
                try {
                    EmitterSignal signal = signalsQueue.take();
                    emittSignal(signal);
                }
                catch(InterruptedException e) {
                    log.error("Unexpected exception!", e);
                }
            }
        };
    }

    private void emittSignal(EmitterSignal signal) {
        log.debug("Signal from emitter to appliances {} ", signal);
        EmitterId emitterId = signal.getEmitterId();
        Collection<ApplianceId> appliancesIds = Optional.ofNullable(applianceBindingsService.boundToEmitter(emitterId)).orElse(Collections.emptySet());

        appliancesIds.forEach(applianceId -> {
            Appliance appliance = appliancesService.applianceById(applianceId);
            if (appliance == null) {
                log.warn("Wrong emitter binding detected, no appliance with id " + applianceId);
            }
            else {
                try {
                    appliancesAgentService.distributeSignal(signal, appliance);
                } catch(SignalException e) {
                    log.error("Signal emission failed. Type {}, appliance {}", signal.getSignalValue().getSignalValueType(), appliance.getId().getValue());
                }
            }
        });
    }
}
