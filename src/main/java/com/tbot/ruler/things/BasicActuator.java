package com.tbot.ruler.things;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class BasicActuator extends AbstractItem<ActuatorId> implements Actuator {

    private Consumer<Message> messageCollectorConsumer;
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
        @NonNull Consumer<Message> messageCollectorConsumer
    ) {
        super(id, name, description);

        if (taskTrigger != null && emissionTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }

        this.messageCollectorConsumer = messageCollectorConsumer;
        this.emissionTask = Optional.ofNullable(emissionTask);
        this.emissionTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);
    }

    @Override
    public void acceptMessage(Message message) {
        messageCollectorConsumer.accept(message);
    }
}
