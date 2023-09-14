package com.tbot.ruler.things;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.MessageReceiver;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
public class BasicActuator extends AbstractItem implements Actuator {

    private Optional<MessageReceiver> messageReceiver;
    private Optional<Runnable> startUpTask;
    private Optional<Runnable> triggerableTask;
    private Optional<TaskTrigger> taskTrigger;

    @Builder
    public BasicActuator(
        @NonNull String id,
        @NonNull String name,
        String description,
        Runnable startUpTask,
        Runnable triggerableTask,
        TaskTrigger taskTrigger,
        MessageReceiver messageReceiver
    ) {
        super(id, name, description);

        if (taskTrigger != null && triggerableTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }

        this.messageReceiver = Optional.ofNullable(messageReceiver);
        this.triggerableTask = Optional.ofNullable(triggerableTask);
        this.taskTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);
    }

    @Override
    public void acceptMessage(Message message) {
        messageReceiver.ifPresent(receiver -> receiver.acceptMessage(message));
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }
}
