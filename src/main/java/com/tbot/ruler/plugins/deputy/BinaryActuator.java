package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.messages.payloads.BooleanTogglePayload;
import com.tbot.ruler.messages.payloads.BooleanUpdatePayload;
import com.tbot.ruler.messages.payloads.UpdateRequestPayload;
import com.tbot.ruler.things.AbstractActuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.thread.RegularEmissionTrigger;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;

import java.util.Optional;

public class BinaryActuator extends AbstractActuator {

    private BinaryActuatorChannel binaryChannel;
    private MessagePublisher messagePublisher;
    private boolean expectedState;

    @Builder
    public BinaryActuator(ActuatorDTO actuatorDTO, BinaryActuatorChannel binaryChannel, MessagePublisher messagePublisher) {
        super(actuatorDTO.getId(), actuatorDTO.getName(), actuatorDTO.getDescription());
        this.binaryChannel = binaryChannel;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        return Optional.of(new RegularEmissionTrigger(600_1000));
    }

    @Override
    public Optional<Runnable> getTriggerableTask() {
        return Optional.of(() -> {
            binaryChannel.updateState(expectedState);
        });
    }

    @Override
    public void acceptMessage(Message message) {
        Object payload = message.getPayloadObject();
        if (payload instanceof BooleanUpdatePayload) {
            handleBooleanUpdate((BooleanUpdatePayload) payload);
        } else if (payload instanceof BooleanTogglePayload) {
            handleToggleUpdate();
        } else if (payload instanceof UpdateRequestPayload) {
            handleUpdateRequest();
        }
    }

    private void handleToggleUpdate() {
        handleStateUpdate(!expectedState);
    }

    private void handleBooleanUpdate(BooleanUpdatePayload payload) {
        handleStateUpdate(payload.isState());
    }

    private void handleStateUpdate(boolean state) {
        expectedState = state;
        binaryChannel.updateState(state);
    }

    private void handleUpdateRequest() {
        expectedState = binaryChannel.requestState();
        Message message = Message.builder()
            .senderId(getId())
            .payload(BooleanUpdatePayload.builder()
                .state(expectedState)
                .build())
            .build();
        messagePublisher.publishMessage(message);
    }
}
