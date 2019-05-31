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
        signalsQueue.add(signal);
        log.debug("Queued signal from appliance: {}", signal);
    }

    public Runnable brokerRunnable() {
        return () -> {
            while(true) {
                try {
                    ApplianceSignal signal = signalsQueue.take();
                    collectSignal(signal);
                }
                catch(InterruptedException e) {
                    log.error("Unexpected exception!", e);
                }
            }
        };
    }

    private void collectSignal(ApplianceSignal signal) {
        log.debug("Signal from appliance to collectors: {}", signal);
        Collection<CollectorId> collectorIds = applianceBindingsService.boundCollectorsIds(signal.getApplianceId());
        collectorIds.forEach(collectorId -> {
            try {
                Collector collector = thingsService.collectorById(collectorId);
                collector.collectSignal(signal);
            }
            catch(CollectorException ex) {
                log.error("Failed to collect signal!", ex);
            }
        });
    }
}
