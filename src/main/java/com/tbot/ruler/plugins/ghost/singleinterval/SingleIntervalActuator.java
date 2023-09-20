package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.things.AbstractActuator;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
public class SingleIntervalActuator extends AbstractActuator {

    private final SingleIntervalStateAgent singleIntervalStateAgent;

    @Builder
    public SingleIntervalActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull SingleIntervalStateAgent singleIntervalStateAgent,
            @NonNull @Singular Collection<Task> asynchronousTasks) {
        super(uuid, name, description, asynchronousTasks);
        this.singleIntervalStateAgent = singleIntervalStateAgent;
    }

    @Override
    public void acceptMessage(Message message) {
        singleIntervalStateAgent.setActive(message.getPayloadAs(OnOffState.class).isOn());
        log.info("Actuator {} active flag changed to {}", this.getUuid(), singleIntervalStateAgent.isActive());
    }
}
