package com.tbot.ruler.plugins.appliance.onoff;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.service.things.SubjectStatePersistenceService;
import com.tbot.ruler.subjects.AbstractActuator;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.Builder;

import static com.tbot.ruler.plugins.StatesUtil.determineOnOffState;

public class OnOffApplianceActuator extends AbstractActuator {

    private final SubjectStatePersistenceService persistenceService;
    private final ActuatorState<OnOffState> state;

    @Builder
    public OnOffApplianceActuator(String uuid, String name, String description, SubjectStatePersistenceService persistenceService) {
        super(uuid, name, description);
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .payload(null)
                .build();
        this.persistenceService = persistenceService;
    }

    @Override
    public void acceptMessage(Message message) {
        OnOffState newStatePayload = determineOnOffState(message, state.getPayload());
        state.updatePayload(newStatePayload);
    }
}
