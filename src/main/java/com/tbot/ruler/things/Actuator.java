package com.tbot.ruler.things;

import com.tbot.ruler.signals.SignalValue;

public interface Actuator {

    public ActuatorId getId();
    public ActuatorMetadata getMetadata();
    public void changeState(SignalValue signal) throws ActuatorException;
    public SignalValue readState() throws ActuatorException;
}
