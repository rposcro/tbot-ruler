package com.tbot.ruler.appliances.agents;

import com.tbot.ruler.exceptions.SignalValueTypeNotMatchingException;
import com.tbot.ruler.appliances.OnOffAppliance;
import com.tbot.ruler.model.state.OnOffValue;
import com.tbot.ruler.signals.OnOffSignalValue;
import com.tbot.ruler.signals.SignalValue;
import com.tbot.ruler.signals.SignalValueType;

public class OnOffApplianceAgent extends ApplianceAgent<OnOffAppliance, OnOffValue> {

    public OnOffApplianceAgent() {
        super(SignalValueType.OnOff, SignalValueType.Toggle);
    }

    @Override
    public void applyToSignal(OnOffAppliance appliance, SignalValue signalValue) throws SignalValueTypeNotMatchingException {
        SignalValueType valueType = signalValue.getSignalValueType();

        if (!acceptsSignalValueType(valueType)) {
            throw new SignalValueTypeNotMatchingException("Signal type " + valueType + " doesn't match this agent!");
        }

        if (SignalValueType.OnOff == valueType) {
            appliance.setStateValue(((OnOffSignalValue) signalValue).isOnSignal() ? OnOffValue.STATE_ON : OnOffValue.STATE_OFF);
        } else {
            appliance.setStateValue(appliance.getStateValue().orElseGet(() -> OnOffValue.STATE_OFF).invert());
        }
    }
}
