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
        log.debug("Queued: signal {} from appliance {}",
            signal.getSignalValue().getSignalValueType(),
            signal.getApplianceId().getValue());
        signalsQueue.add(signal);
    }

    public Runnable brokerRunnable() {
        return () -> {
            while(true) {
                try {
                    ApplianceSignal signal = signalsQueue.take();
                    sendSignal(signal);
                } catch(InterruptedException e) {
                    log.error("Unexpected interruption!", e);
                } catch(Exception e) {
                    log.error("Uncaught exception while sending signal!", e);
                }
            }
        };
    }

    private void sendSignal(ApplianceSignal signal) {
        ActuatorId actuatorId = applianceBindingsService.boundActuatorId(signal.getApplianceId());
        Actuator actuator = thingsService.actuatorById(actuatorId);

        if (actuator != null) {
            log.debug("Sending: signal from appliance {} to actuator {} of type {}",
                signal.getApplianceId().getValue(),
                actuatorId.getValue(),
                actuator.getClass().getSimpleName());
            try {
                actuator.changeState(signal.getSignalValue());
            }
            catch(ActuatorException ex) {
                log.error("Failed to send signal to actuator!", ex);
            }
        }
        else {
            log.warn("Note! Appliance {} has no actuator defined, signal skipped!", signal.getApplianceId().getValue());
        }
    }
}
