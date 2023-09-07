package com.tbot.ruler.things;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class BasicEmitter extends AbstractItem implements Emitter {

    private Optional<Runnable> startUpTask;
    private Optional<Runnable> triggerableTask;
    private Optional<TaskTrigger> taskTrigger;
    private Optional<Consumer<MessageDeliveryReport>> reportListener;

    @Builder
    public BasicEmitter(
        @NonNull String id,
        @NonNull String name,
        String description,
        Runnable startUpTask,
        Runnable triggerableTask,
        TaskTrigger taskTrigger,
        Consumer<MessageDeliveryReport> reportListener
    ) {
        super(id, name, description);
        this.triggerableTask = Optional.ofNullable(triggerableTask);
        this.taskTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);
        this.reportListener = Optional.ofNullable(reportListener);

        if (taskTrigger != null && triggerableTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        reportListener.ifPresent(listener -> listener.accept(deliveryReport));
    }
}
