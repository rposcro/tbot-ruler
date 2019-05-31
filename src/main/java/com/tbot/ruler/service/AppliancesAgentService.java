package com.tbot.ruler.service;

import com.tbot.ruler.broker.ActuatorBroker;
import com.tbot.ruler.broker.SignalCollectionBroker;
import com.tbot.ruler.exceptions.SignalException;
import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceClass;
import com.tbot.ruler.appliances.agents.ApplianceAgent;
import com.tbot.ruler.signals.ApplianceSignal;
import com.tbot.ruler.signals.EmitterSignal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AppliancesAgentService {

    @Autowired
    private ActuatorBroker actuatorBroker;

    @Autowired
    private SignalCollectionBroker collectionBroker;

    @Autowired
    private Map<ApplianceClass, ApplianceAgent> agentsPerApplianceClass;

    public void distributeSignal(EmitterSignal signal, Appliance appliance) throws SignalException {
        log.debug("Received signal {} from emitter {} for appliance {}",
            signal.getSignalValue().getSignalValueType(),
            signal.getEmitterId().getValue(),
            appliance.getId().getValue());
        findAgent(appliance).applyToSignal(appliance, signal.getSignalValue());
        ApplianceSignal applianceSignal = new ApplianceSignal(signal.getSignalValue(), appliance.getId());
        actuatorBroker.sendSignalToActuator(applianceSignal);
        collectionBroker.distributeSignalToCollectors(applianceSignal);
    }

    private ApplianceAgent findAgent(Appliance appliance) {
        ApplianceClass applianceClass = appliance.getClass().getAnnotationsByType(ApplianceClass.class)[0];
        return agentsPerApplianceClass.get(applianceClass);
    }
}
