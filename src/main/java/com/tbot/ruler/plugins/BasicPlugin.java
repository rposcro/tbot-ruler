package com.tbot.ruler.plugins;

import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class BasicPlugin implements Plugin {

    private String uuid;
    private String name;
    @Builder.Default
    private List<? extends Thing> things = Collections.emptyList();

    private Optional<Runnable> startUpTask;
    private Optional<Runnable> triggerableTask;
    private Optional<TaskTrigger> taskTrigger;

    @Builder
    public BasicPlugin(
            String uuid,
            String name,
            List<? extends Thing> things,
            Runnable startUpTask,
            Runnable triggerableTask,
            TaskTrigger taskTrigger) {
        this.uuid = uuid;
        this.name = name;
        this.things = things;
        this.startUpTask = Optional.ofNullable(startUpTask);
        this.triggerableTask = Optional.ofNullable(triggerableTask);
        this.taskTrigger = Optional.ofNullable(taskTrigger);
    }
}
