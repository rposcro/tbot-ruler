package com.tbot.ruler.things;

import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class BasicThing extends AbstractItem implements Thing {

    private String uuid;
    private String name;
    private String description;
    @Builder.Default
    private List<? extends Actuator> actuators = Collections.emptyList();

    private Optional<Runnable> startUpTask;
    private Optional<Runnable> triggerableTask;
    private Optional<TaskTrigger> taskTrigger;

    @Builder
    public BasicThing(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        Runnable startUpTask,
        Runnable triggerableTask,
        TaskTrigger taskTrigger,
        List<? extends Actuator> actuators
    ) {
        super(uuid, name, description);

        if (taskTrigger != null && triggerableTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }

        this.triggerableTask = Optional.ofNullable(triggerableTask);
        this.taskTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);
        this.actuators = actuators != null ? actuators : Collections.emptyList();
    }
}
