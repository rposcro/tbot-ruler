package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.agents.OnOffApplianceAgent;
import com.tbot.ruler.model.state.OnOffValue;
import lombok.Getter;

import java.util.Optional;

@ApplianceClass(agent = OnOffApplianceAgent.class)
public class OnOffAppliance extends Appliance<OnOffValue> {

    public OnOffAppliance(ApplianceId applianceId) {
        super(applianceId);
    }

    @Getter
    private Optional<OnOffValue> stateValue = Optional.empty();

    @Override
    public void setStateValue(OnOffValue signalValue) {
        stateValue = Optional.ofNullable(signalValue);
    }
}
