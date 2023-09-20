package com.tbot.ruler.things;

import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Collection;
import java.util.Optional;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public class BasicActuator extends AbstractItem implements Actuator {

    private final Optional<MessageReceiver> messageReceiver;
    private final Collection<Task> asynchronousTasks;

    @Builder
    public BasicActuator(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        @Singular Collection<Task> asynchronousTasks,
        MessageReceiver messageReceiver
    ) {
        super(uuid, name, description);

        this.messageReceiver = Optional.ofNullable(messageReceiver);
        this.asynchronousTasks = orEmpty(asynchronousTasks);
    }

    @Override
    public void acceptMessage(Message message) {
        messageReceiver.ifPresent(receiver -> receiver.acceptMessage(message));
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }
}
