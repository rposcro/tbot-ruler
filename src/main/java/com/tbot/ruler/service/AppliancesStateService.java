package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.exceptions.SignalException;
import com.tbot.ruler.signals.EmitterSignal;
import com.tbot.ruler.signals.OnOffSignalValue;
import com.tbot.ruler.things.EmitterId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppliancesStateService {

    private static final EmitterId EMITTER_SERVICE_ID = new EmitterId("rest-service");

    @Autowired
    private AppliancesService appliancesService;

    @Autowired
    private ApplianceAgentService applianceAgentService;

    public void changeStateValue(ApplianceId applianceId, OnOffSignalValue signalValue) throws SignalException {
        Appliance appliance = appliancesService.applianceById(applianceId);
        EmitterSignal signal = new EmitterSignal(signalValue, EMITTER_SERVICE_ID);
        applianceAgentService.distributeSignal(signal, appliance);
    }
}
