package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;

@Getter
public class CentralSceneHoldActuator extends AbstractActuator {

    private final long minMillisecondsOfHold;
    private final long maxMillisecondsOfHold;
    private final MessagePublisher messagePublisher;
    private final Message message;

    private long lastHoldTimestamp;

    @Builder
    public CentralSceneHoldActuator(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        long minMillisecondsOfHold,
        long maxMillisecondsOfHold,
        @NonNull MessagePublisher messagePublisher
    ) {
        super(uuid, name, description, Collections.emptyList());
        this.minMillisecondsOfHold = minMillisecondsOfHold;
        this.maxMillisecondsOfHold = maxMillisecondsOfHold;
        this.messagePublisher = messagePublisher;
        this.message = Message.builder()
            .senderId(uuid)
            .payload(BinaryStateClaim.TOGGLE)
            .build();
    }

    public void handleCommandKey(CentralSceneKeyAttribute keyAttribute) {
        if (keyAttribute == CentralSceneKeyAttribute.KEY_HELD_DOWN) {
            lastHoldTimestamp = System.currentTimeMillis();
        } else if (keyAttribute == CentralSceneKeyAttribute.KEY_RELEASED) {
            long holdDuration = System.currentTimeMillis() - lastHoldTimestamp;
            lastHoldTimestamp = 0;
            if (holdDuration >= minMillisecondsOfHold && holdDuration <= maxMillisecondsOfHold) {
                messagePublisher.publishMessage(message);
            }
        }
    }
}
