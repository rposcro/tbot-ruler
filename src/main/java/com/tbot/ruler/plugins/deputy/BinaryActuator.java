package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.model.Notification;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.things.AbstractActuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.thread.RegularEmissionTrigger;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class BinaryActuator extends AbstractActuator {

    private BinaryActuatorChannel binaryChannel;
    private MessagePublisher messagePublisher;
    private boolean expectedState;

    @Builder
    public BinaryActuator(ActuatorDTO actuatorDTO, BinaryActuatorChannel binaryChannel, MessagePublisher messagePublisher) {
        super(actuatorDTO.getUuid(), actuatorDTO.getName(), actuatorDTO.getDescription());
        this.binaryChannel = binaryChannel;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        return Optional.of(new RegularEmissionTrigger(600_1000));
    }

    @Override
    public Optional<Runnable> getTriggerableTask() {
        return Optional.of(() -> binaryChannel.updateState(expectedState));
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
