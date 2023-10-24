package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class SingleIntervalActuator extends AbstractActuator {

    private final SingleIntervalAgent singleIntervalAgent;

    @Builder
    public SingleIntervalActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull SingleIntervalAgent singleIntervalAgent,
            @NonNull @Singular Collection<Task> asynchronousTasks) {
        super(uuid, name, description, asynchronousTasks);
        this.singleIntervalAgent = singleIntervalAgent;
    }

    @Override
    public void acceptMessage(Message message) {
        singleIntervalAgent.setActivated(message.getPayloadAs(OnOffState.class).isOn());
        log.info("Ghost actuator {} activation flag changed to {}", this.getUuid(), singleIntervalAgent.isActivated());
    }

    @Override
    public ActuatorState getState() {
        return singleIntervalAgent.getCurrentState();
    }
}
