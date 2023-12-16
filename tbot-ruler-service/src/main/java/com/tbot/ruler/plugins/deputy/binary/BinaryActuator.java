package com.tbot.ruler.plugins.deputy.binary;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.Notification;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class BinaryActuator extends AbstractSubject implements Actuator {

    private final BinaryActuatorChannel binaryChannel;
    private final MessagePublisher messagePublisher;

    private boolean expectedState;

    @Builder
    public BinaryActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull BinaryActuatorChannel binaryChannel,
            @NonNull MessagePublisher messagePublisher) {
        super(uuid, name, description);
        this.binaryChannel = binaryChannel;
        this.messagePublisher = messagePublisher;
        this.jobBundles = Collections.singleton(JobBundle.periodicalJobBundle(
                () -> binaryChannel.updateState(expectedState), 600_000));
    }

    @Override
    public void acceptMessage(Message message) {
        Object payload = message.getPayload();
        if (payload instanceof OnOffState) {
            handleStateUpdate(((OnOffState) payload).isOn());
        } else if (payload instanceof Notification) {
            handleUpdateRequest();
        } else {
            log.warn("BinaryActuator doesn't handle {} payloads", payload.getClass());
        }
    }

    private void handleStateUpdate(boolean state) {
        expectedState = state;
        binaryChannel.updateState(state);
    }

    private void handleUpdateRequest() {
        expectedState = binaryChannel.requestState();
        Message message = Message.builder()
            .senderId(getUuid())
            .payload(OnOffState.of(expectedState))
            .build();
        messagePublisher.publishMessage(message);
    }
}
