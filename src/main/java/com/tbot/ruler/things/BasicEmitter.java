package com.tbot.ruler.things;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
public class BasicEmitter extends AbstractItem<EmitterId> implements Emitter {

    private Optional<Runnable> startUpTask;
    private Optional<Runnable> triggerableTask;
    private Optional<TaskTrigger> taskTrigger;

    @Builder
    public BasicEmitter(
        @NonNull EmitterId id,
        @NonNull String name,
        String description,
        Runnable startUpTask,
        Runnable triggerableTask,
        TaskTrigger taskTrigger
    ) {
        super(id, name, description);
        this.triggerableTask = Optional.ofNullable(triggerableTask);
        this.taskTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);

        if (taskTrigger != null && triggerableTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
    }
}
