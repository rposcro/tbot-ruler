package com.tbot.ruler.service.things;

import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.task.EmissionTriggerContext;
import com.tbot.ruler.task.Task;
import com.tbot.ruler.task.TaskTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@Scope("singleton")
public class TasksLifetimeService {

    @Autowired
    private SubjectLifetimeService subjectLifetimeService;
    @Autowired
    private ThreadPoolTaskScheduler triggerableTasksScheduler;
    @Autowired
    private ConcurrentTaskExecutor continuousTasksExecutor;
    @Autowired
    private ConcurrentTaskExecutor startUpTasksExecutor;

    @PostConstruct
    public void init() {
        List<Subject> subjects = subjects();
        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousTasks().stream())
                .filter(Task::runOnStartUp)
                .map(Task::getRunnable)
                .forEach(runnable -> startUpTasksExecutor.execute(runnable));

        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousTasks().stream())
                .filter(Task::runContinuously)
                .map(Task::getRunnable)
                .forEach(runnable -> continuousTasksExecutor.execute(runnable));

        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousTasks().stream())
                .filter(Task::runOnTrigger)
                .forEach(task -> {
                    Runnable runnable = task.getRunnable();
                    TaskTrigger trigger = task.getTaskTrigger();
                    triggerableTasksScheduler.schedule(
                            runnable,
                            context -> {
                                Date nextEmissionTime = trigger.nextEmissionTime(new EmissionTriggerContext(context.lastScheduledExecutionTime()));
                                log.info("Next emission time for {} is {}", task, nextEmissionTime);
                                return nextEmissionTime;
                            });
                });
    }

    private List<Subject> subjects() {
        LinkedList<Subject> subjects = new LinkedList<>();
        subjects.addAll(subjectLifetimeService.getAllPlugins());
        subjects.addAll(subjectLifetimeService.getAllThings());
        subjects.addAll(subjectLifetimeService.getAllActuators());
        return subjects;
    }
}
