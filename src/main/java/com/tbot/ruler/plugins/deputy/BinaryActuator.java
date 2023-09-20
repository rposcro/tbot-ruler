package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.Notification;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.things.AbstractActuator;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.threads.RegularEmissionTrigger;
import com.tbot.ruler.threads.Task;
import com.tbot.ruler.threads.TaskTrigger;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
public class BinaryActuator extends AbstractItem implements Actuator {


    private final BinaryActuatorChannel binaryChannel;
    private final MessagePublisher messagePublisher;
    private final Collection<Task> asynchronousTasks;

    private boolean expectedState;

    @Builder
    public BinaryActuator(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull BinaryActuatorChannel binaryChannel,
            @NonNull MessagePublisher messagePublisher) {
        super(id, name, description);
        this.binaryChannel = binaryChannel;
        this.messagePublisher = messagePublisher;
        this.asynchronousTasks = Collections.singleton(Task.triggerableTask(
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
