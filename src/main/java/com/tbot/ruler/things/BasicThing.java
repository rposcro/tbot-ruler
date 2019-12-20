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

    private ItemId id;
    private String name;
    private String description;
    @Builder.Default
    private List<? extends Emitter> emitters = Collections.emptyList();
    @Builder.Default
    private List<? extends Collector> collectors = Collections.emptyList();
    @Builder.Default
    private List<? extends Actuator> actuators = Collections.emptyList();

    private Optional<Runnable> startUpTask;
    private Optional<Runnable> triggerableTask;
    private Optional<TaskTrigger> taskTrigger;

    @Builder
    public BasicThing(
        @NonNull ItemId id,
        @NonNull String name,
        String description,
        Runnable startUpTask,
        Runnable triggerableTask,
        TaskTrigger taskTrigger,
        List<? extends Emitter> emitters,
        List<? extends Collector> collectors,
        List<? extends Actuator> actuators
    ) {
        super(id, name, description);

        if (taskTrigger != null && triggerableTask == null) {
            throw new IllegalArgumentException("Emission trigger is only allowed when emission task is specified!");
        }

        this.triggerableTask = Optional.ofNullable(triggerableTask);
        this.taskTrigger = Optional.ofNullable(taskTrigger);
        this.startUpTask = Optional.ofNullable(startUpTask);
        this.emitters = emitters;
        this.collectors = collectors;
        this.actuators = actuators;
    }
}
