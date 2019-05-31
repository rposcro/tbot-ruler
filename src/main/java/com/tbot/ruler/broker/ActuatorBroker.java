package com.tbot.ruler.broker;

import com.tbot.ruler.service.things.ApplianceBindingsService;
import com.tbot.ruler.service.things.ThingsService;
import com.tbot.ruler.signals.ApplianceSignal;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.ActuatorException;
import com.tbot.ruler.things.ActuatorId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class ActuatorBroker {

    @Autowired
    private ThingsService thingsService;
    @Autowired
    private ApplianceBindingsService applianceBindingsService;

    private LinkedBlockingQueue<ApplianceSignal> signalsQueue;

    @Autowired
    public ActuatorBroker(@Value("${ruler.broker.actuationQueueLength:50}") int queueSize) {
        log.info("Actuation queue length set to {}", queueSize);
        signalsQueue = new LinkedBlockingQueue<>(queueSize);
    }

    public void sendSignalToActuator(ApplianceSignal signal) {
        signalsQueue.add(signal);
        log.debug("Queued signal from appliance: {}", signal);
    }

    public Runnable brokerRunnable() {
        return () -> {
            while(true) {
                try {
                    ApplianceSignal signal = signalsQueue.take();
                    sendSignal(signal);
                }
                catch(InterruptedException e) {
                    log.error("Unexpected exception!", e);
                }
            }
        };
    }

    private void sendSignal(ApplianceSignal signal) {
        log.debug("Sending signal from appliance: {}", signal);
        ActuatorId actuatorId = applianceBindingsService.boundActuatorId(signal.getApplianceId());
        Actuator actuator = thingsService.actuatorById(actuatorId);

        if (actuator != null) {
            try {
                actuator.changeState(signal.getSignalValue());
            }
            catch(ActuatorException ex) {
                log.error("Failed to send signal to actuator!", ex);
            }
        }
        else {
            log.warn("Note! Appliance {} has no actuator defined, signal {} skipped!", signal.getApplianceId(), signal);
        }
    }
}
