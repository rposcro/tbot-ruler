package com.tbot.ruler.broker;

import com.tbot.ruler.service.things.ApplianceBindingsService;
import com.tbot.ruler.service.things.ThingsService;
import com.tbot.ruler.signals.ApplianceSignal;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.CollectorException;
import com.tbot.ruler.things.CollectorId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
public class SignalCollectionBroker {

    @Autowired
    private ThingsService thingsService;
    @Autowired
    private ApplianceBindingsService applianceBindingsService;

    private LinkedBlockingQueue<ApplianceSignal> signalsQueue;

    @Autowired
    public SignalCollectionBroker(@Value("${ruler.broker.collectionQueueLength:50}") int queueSize) {
        log.info("Collection queue length set to {}", queueSize);
        signalsQueue = new LinkedBlockingQueue<>(queueSize);
    }

    public void distributeSignalToCollectors(ApplianceSignal signal) {
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
                    collectSignal(signal);
                } catch(InterruptedException e) {
                    log.error("Unexpected interruption!", e);
                } catch(Exception e) {
                    log.error("Uncaught exception while sending signal!", e);
                }
            }
        };
    }

    private void collectSignal(ApplianceSignal signal) {
        Collection<CollectorId> collectorIds = applianceBindingsService.boundCollectorsIds(signal.getApplianceId());

        if (collectorIds.isEmpty()) {
            log.debug("Skipping: no addressee for signal {} from appliance {}",
                signal.getSignalValue().getSignalValueType(),
                signal.getApplianceId().getValue());
        } else {
            collectorIds.forEach(collectorId -> {
                try {
                    Collector collector = thingsService.collectorById(collectorId);
                    log.debug("Sending: signal from appliance {} to collector {} of class {}",
                        signal.getApplianceId().getValue(),
                        collectorId.getValue(),
                        collector.getClass().getSimpleName());
                    collector.collectSignal(signal);
                }
                catch(CollectorException ex) {
                    log.error("Failed to collect signal!", ex);
                }
            });
        }
    }
}
