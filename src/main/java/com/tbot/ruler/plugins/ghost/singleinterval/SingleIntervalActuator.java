package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.AbstractActuator;
import com.tbot.ruler.subjects.SubjectState;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class SingleIntervalActuator extends AbstractActuator {

    private final SingleIntervalActuatorState singleIntervalActuatorState;

    @Builder
    public SingleIntervalActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull SingleIntervalActuatorState singleIntervalActuatorState,
            @NonNull @Singular Collection<Task> asynchronousTasks) {
        super(uuid, name, description, asynchronousTasks);
        this.singleIntervalActuatorState = singleIntervalActuatorState;
    }

    @Override
    public void acceptMessage(Message message) {
        singleIntervalActuatorState.setActive(message.getPayloadAs(OnOffState.class).isOn());
        log.info("Actuator {} active flag changed to {}", this.getUuid(), singleIntervalActuatorState.isActive());
    }

    @Override
    public SubjectState getState() {
        return this.singleIntervalActuatorState;
    }
}
