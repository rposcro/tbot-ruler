package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.state.CompoundState;
import com.tbot.ruler.appliances.state.State;
import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.things.ApplianceId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundAppliance extends AbstractAppliance<CompoundState> {

    private Map<ApplianceId, Appliance> componentsMap;

    public CompoundAppliance(ApplianceId id, String name, String description, List<Appliance> components) {
        super(id, name, description);
        this.componentsMap = components.stream()
            .peek(appliance -> { if (appliance instanceof CompoundAppliance) throw new ConfigurationException("Compound appliances of multiple levels not supported!"); })
            .collect(Collectors.toMap(
                Appliance::getId,
                Function.identity(),
                (u, v) -> { throw new ConfigurationException("Duplicated appliance id detected!"); },
                LinkedHashMap::new
        ));
    }

    public Optional<CompoundState> getState() {
        Map<String, State> stateMap = componentsMap.values().stream()
            .collect(Collectors.toMap(
                appliance -> appliance.getId().getValue(),
                appliance -> (State) appliance.getState().orElse(null),
                (u, v) -> { throw new IllegalStateException("Unexpected keys collision!"); },
                () -> new LinkedHashMap<String, State>())
            );
        return Optional.of(new CompoundState(stateMap));
    }

    @Override
    public Optional<Message> acceptDirectPayload(MessagePayload payload) {
        return Optional.empty();
    }

    @Override
    public void acceptMessage(Message message) {
        throw new MessageUnsupportedException("Compound appliances don't support messages, use child components!");
    }
}
