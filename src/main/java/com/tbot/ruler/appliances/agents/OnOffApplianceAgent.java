package com.tbot.ruler.appliances.agents;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.exceptions.SignalValueTypeNotMatchingException;
import com.tbot.ruler.appliances.OnOffAppliance;
import com.tbot.ruler.model.state.OnOffValue;
import com.tbot.ruler.signals.ApplianceSignal;
import com.tbot.ruler.signals.OnOffSignalValue;
import com.tbot.ruler.signals.SignalValue;
import com.tbot.ruler.signals.SignalValueType;
import com.tbot.ruler.message.payloads.BinarySetPayload;
import com.tbot.ruler.things.model.message.TBotMessage;

import static com.tbot.ruler.model.state.OnOffValue.STATE_OFF;
import static com.tbot.ruler.model.state.OnOffValue.STATE_ON;

public class OnOffApplianceAgent extends ApplianceAgent<OnOffAppliance, OnOffValue> {

    public OnOffApplianceAgent() {
        super(SignalValueType.OnOff, SignalValueType.Toggle);
    }

    @Override
    public ApplianceSignal applyToSignal(OnOffAppliance appliance, SignalValue signalValue) throws SignalValueTypeNotMatchingException {
        SignalValueType valueType = signalValue.getSignalValueType();

        if (!acceptsSignalValueType(valueType)) {
            throw new SignalValueTypeNotMatchingException("Signal type " + valueType + " doesn't match this agent!");
        }

        OnOffValue newValue = SignalValueType.OnOff == valueType ?
            ((OnOffSignalValue) signalValue).isOnSignal() ? STATE_ON : STATE_OFF :
            appliance.getStateValue().orElseGet(() -> STATE_OFF).invert();
        appliance.setStateValue(newValue);
        return new ApplianceSignal(newValue == STATE_ON ? OnOffSignalValue.ON_SIGNAL_VALUE : OnOffSignalValue.OFF_SIGNAL_VALUE, appliance.getId());
    }

    @Override
    public void applyMessage(TBotMessage tBotMessage, Appliance appliance) {
        BinarySetPayload binarySetMessage = tBotMessage.ensureMessageType();
        appliance.setStateValue(binarySetMessage.isOn() ? STATE_ON : STATE_OFF);
    }
}
