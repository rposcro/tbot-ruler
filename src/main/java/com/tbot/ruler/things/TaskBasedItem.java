package com.tbot.ruler.things;

import com.tbot.ruler.things.thread.TaskTrigger;

import java.util.Optional;

public interface TaskBasedItem<T extends ItemId> extends Item<T> {

    default Optional<Runnable> getTriggerableTask() {
        return Optional.empty();
    }

    default Optional<TaskTrigger> getTaskTrigger() {
        return Optional.empty();
    }

    default Optional<Runnable> getStartUpTask() {
        return Optional.empty();
    }
}
