package com.tbot.ruler.appliances;

import com.tbot.ruler.model.state.StateValue;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
public abstract class Appliance<T extends StateValue> {

    private ApplianceId id;
    @Setter
    private String name;
    @Setter
    private String description;
    private Optional<T> stateValue;

    protected Appliance(ApplianceId applianceId) {
        this.id = applianceId;
    }

    public abstract Optional<T> getStateValue();
    public abstract void setStateValue(T signalValue);
}
