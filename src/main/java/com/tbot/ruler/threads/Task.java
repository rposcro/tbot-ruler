package com.tbot.ruler.threads;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class Task {

    private final Runnable runnable;
    private final TaskTrigger taskTrigger;
    private final boolean runContinuously;

    public boolean runOnTrigger() {
        return taskTrigger != null;
    }

    public boolean runOnStartUp() {
        return taskTrigger == null && !runContinuously;
    }

    public boolean runContinuously() {
        return taskTrigger == null && runContinuously;
    }

    public static Task triggerableTask(@NonNull Runnable runnable, @NonNull TaskTrigger taskTrigger) {
        return new Task(runnable, taskTrigger, true);
    }

    public static Task triggerableTask(@NonNull Runnable runnable, long periodMilliseconds) {
        return new Task(runnable, new RegularEmissionTrigger(periodMilliseconds), true);
    }

    public static Task startUpTask(@NonNull Runnable runnable) {
        return new Task(runnable, null, false);
    }

    public static Task continuousTask(Runnable runnable) {
        return new Task(runnable, null, true);
    }
}
