package com.tbot.ruler.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public final class SubjectTask {

    private final Runnable runnable;
    private final TaskTrigger taskTrigger;
    private final boolean runContinuously;

    public boolean runsOnTrigger() {
        return taskTrigger != null;
    }

    public boolean runsOnStartUp() {
        return taskTrigger == null && !runContinuously;
    }

    public boolean runsContinuously() {
        return taskTrigger == null && runContinuously;
    }

    public static SubjectTask triggerableTask(@NonNull Runnable runnable, @NonNull TaskTrigger taskTrigger) {
        return new SubjectTask(runnable, taskTrigger, true);
    }

    public static SubjectTask triggerableTask(@NonNull Runnable runnable, long periodMilliseconds) {
        return new SubjectTask(runnable, new RegularEmissionTrigger(periodMilliseconds), true);
    }

    public static SubjectTask startUpTask(@NonNull Runnable runnable) {
        return new SubjectTask(runnable, null, false);
    }

    public static SubjectTask continuousTask(Runnable runnable) {
        return new SubjectTask(runnable, null, true);
    }
}
