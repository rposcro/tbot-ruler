package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;

@Getter
public class CentralSceneHoldActuator extends AbstractActuator {

    private final long minMillisecondsOfHold;
    private final long maxMillisecondsOfHold;
    private final CentralSceneActuatorMode mode;

    private final SubjectStateService stateService;
    private final MessagePublisher messagePublisher;

    private long lastHoldTimestamp;
    private ActuatorState<BinaryState> state;

    @Builder
    public CentralSceneHoldActuator(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        long minMillisecondsOfHold,
        long maxMillisecondsOfHold,
        @NonNull CentralSceneActuatorMode mode,
        @NonNull MessagePublisher messagePublisher,
        @NonNull SubjectStateService stateService
    ) {
        super(uuid, name, description, Collections.emptyList());
        this.minMillisecondsOfHold = minMillisecondsOfHold;
        this.maxMillisecondsOfHold = maxMillisecondsOfHold;
        this.mode = mode;
        this.messagePublisher = messagePublisher;
        this.stateService = stateService;

        initState();
    }

    public void handleCommandKey(CentralSceneKeyAttribute keyAttribute) {
        if (keyAttribute == CentralSceneKeyAttribute.KEY_HELD_DOWN) {
            lastHoldTimestamp = System.currentTimeMillis();
        } else if (keyAttribute == CentralSceneKeyAttribute.KEY_RELEASED) {
            long holdDuration = System.currentTimeMillis() - lastHoldTimestamp;
            lastHoldTimestamp = 0;
            if (holdDuration >= minMillisecondsOfHold && holdDuration <= maxMillisecondsOfHold) {
                messagePublisher.publishMessage(buildMessage());
            }
        }
    }

    private void setState(BinaryState state) {
        this.state.updatePayload(state);
        stateService.persistState(this.state);
    }

    private void initState() {
        if (mode == CentralSceneActuatorMode.STATEFUL) {
            state = stateService.recoverActuatorState(uuid, BinaryState.class)
                .orElseGet(() -> stateService.persistState(ActuatorState.builder()
                    .actuatorUuid(uuid)
                    .payload(BinaryState.OFF)
                    .build()));
        }
    }

    private Message buildMessage() {
        if (mode == CentralSceneActuatorMode.STATELESS) {
            return Message.builder()
                .senderId(uuid)
                .payload(BinaryClaim.TOGGLE)
                .build();
        } else {
            setState(state.getPayload().negate());
            return Message.builder()
                .senderId(uuid)
                .payload(state.getPayload() == BinaryState.ON ? BinaryClaim.SET_ON : BinaryClaim.SET_OFF)
                .build();
        }
    }
}
