package com.tbot.ruler.service.things;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.configuration.BindingsConfiguration;
import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.CollectorId;
import com.tbot.ruler.things.EmitterId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ApplianceBindingsService {

    @Autowired
    private BindingsConfiguration bindingsConfiguration;

    public Collection<CollectorId> boundCollectorsIds(ApplianceId applianceId) {
        return bindingsConfiguration.appliancesToCollectorsMap().get(applianceId);
    }

    public ActuatorId boundActuatorId(ApplianceId applianceId) {
        return bindingsConfiguration.applianceToActuatorMap().get(applianceId);
    }

    public Collection<ApplianceId> boundToEmitter(EmitterId emitterId) {
        return bindingsConfiguration.emittersToAppliancesMap().get(emitterId);
    }
}
