package com.tbot.ruler.things;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessageReceiver;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
public class BasicActuator extends AbstractItem<ActuatorId> implements Actuator {

    private Optional<MessageReceiver> messageReceiver;
    private Optional<Runnable> startUpTask;
    private Optional<Runnable> emissionTask;
    private Optional<TaskTrigger> emissionTrigger;

    @Builder
    public BasicActuator(
        @NonNull ActuatorId id,
        @NonNull String name,
        String description,
        Runnable startUpTask,
        Runnable emissionTask,
        TaskTrigger taskTrigger,
        MessageReceiver messageReceiver
    ) {
        super(id, name, description);

        if (taskTrigger != null && emissionTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }

        this.messageReceiver = Optional.ofNullable(messageReceiver);
        this.emissionTask = Optional.ofNullable(emissionTask);
        this.emissionTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);
    }

    @Override
    public void acceptMessage(Message message) {
        messageReceiver.ifPresent(receiver -> receiver.acceptMessage(message));
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
    }
}
