package com.tbot.ruler.plugins.agent.signaler;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SignalerActuator extends AbstractActuator {

    private final ActuatorState<OnOffState> state;
    private final Message signalMessage;
    private final MessagePublisher messagePublisher;

    @Builder
    public SignalerActuator(
            @NonNull ActuatorEntity actuatorEntity,
            @NonNull Object signalValue,
            @NonNull RulerThingContext thingContext) {
        super(actuatorEntity.getActuatorUuid(), actuatorEntity.getName(), actuatorEntity.getDescription());
        this.messagePublisher = thingContext.getMessagePublisher();
        this.signalMessage = Message.builder()
                .senderId(actuatorEntity.getActuatorUuid())
                .payload(signalValue)
                .build();
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .payload(OnOffState.STATE_ON)
                .build();
    }

    @Override
    public void triggerAction() {
        messagePublisher.publishMessage(signalMessage);
    }
}
