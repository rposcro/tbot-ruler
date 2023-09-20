package com.tbot.ruler.plugins.ghost.singleinterval;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.things.AbstractActuator;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuperBuilder
public class SingleIntervalActuator extends AbstractActuator {

    private SingleIntervalStateAgent singleIntervalStateAgent;

    @Override
    public void acceptMessage(Message message) {
        singleIntervalStateAgent.setActive(message.getPayloadAs(OnOffState.class).isOn());
        log.info("Actuator {} active flag changed to {}", this.getUuid(), singleIntervalStateAgent.isActive());
    }
}
