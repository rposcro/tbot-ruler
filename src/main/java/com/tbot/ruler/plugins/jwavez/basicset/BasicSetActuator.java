package com.tbot.ruler.plugins.jwavez.basicset;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.ActuatorId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class BasicSetActuator extends AbstractItem<ActuatorId> implements Actuator {

    private BasicSetEmissionProducer emissionProducer;
    private BasicSetMessageConsumer messageConsumer;

    @Builder
    public BasicSetActuator(
        @NonNull ActuatorId id,
        @NonNull String name,
        String description,
        @NonNull BasicSetMessageConsumer messageConsumer,
        @NonNull BasicSetEmissionProducer emissionProducer) {
        super(id, name, description);
        this.messageConsumer = messageConsumer;
        this.emissionProducer = emissionProducer;
    }

    @Override
    public void acceptMessage(Message message) {
        messageConsumer.accept(message);
    }
}
