package com.tbot.ruler.appliances.agents;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.exceptions.SignalException;
import com.tbot.ruler.model.state.StateValue;
import com.tbot.ruler.signals.SignalValue;
import com.tbot.ruler.signals.SignalValueType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ApplianceAgent<A extends Appliance, S extends StateValue> {

    private Set<SignalValueType> acceptedSignalTypes;

    protected ApplianceAgent(SignalValueType... acceptedSignalTypes) {
        this.acceptedSignalTypes = Stream.of(acceptedSignalTypes).collect(Collectors.toSet());
    }

    public boolean acceptsSignalValueType(SignalValueType valueType) {
        return acceptedSignalTypes.contains(valueType);
    }

    public abstract void applyToSignal(A appliance, SignalValue signalValue) throws SignalException;
}
