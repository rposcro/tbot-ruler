package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.task.EmissionTriggerContext;
import com.tbot.ruler.task.SubjectTask;
import com.tbot.ruler.task.TaskTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@Scope("singleton")
public class SubjectsTasksLifecycleService {

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @Autowired
    private ThingsLifecycleService thingsLifecycleService;

    @Autowired
    private ActuatorsLifecycleService actuatorsLifecycleService;

    @Autowired
    private ThreadPoolTaskScheduler triggerableTasksScheduler;

    @Autowired
    private ConcurrentTaskExecutor continuousTasksExecutor;

    @Autowired
    private ConcurrentTaskExecutor startUpTasksExecutor;

    public void startUpAllTasks() {
        List<Subject> subjects = subjects();
        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousSubjectTasks().stream())
                .filter(SubjectTask::runsOnStartUp)
                .forEach(task -> {
                    log.info("Task Lifecycle: Executing start up task {}", task);
                    startUpTasksExecutor.execute(task.getRunnable());
                });

        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousSubjectTasks().stream())
                .filter(SubjectTask::runsContinuously)
                .forEach(task -> {
                    log.info("Task Lifecycle: Executing continuous task {}", task);
                    continuousTasksExecutor.execute(task.getRunnable());
                });

        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousSubjectTasks().stream())
                .filter(SubjectTask::runsOnTrigger)
                .forEach(task -> {
                    Runnable runnable = task.getRunnable();
                    TaskTrigger trigger = task.getTaskTrigger();
                    log.info("Task Lifecycle: Executing trigerrable task {}", task);
                    triggerableTasksScheduler.schedule(
                            runnable,
                            context -> {
                                Date nextEmissionTime = trigger.nextEmissionTime(new EmissionTriggerContext(context.lastScheduledExecutionTime()));
                                log.info("Task Lifecycle: Next emission time for {} is {}", task, nextEmissionTime);
                                return nextEmissionTime.toInstant();
                            });
                });
    }

    private List<Subject> subjects() {
        LinkedList<Subject> subjects = new LinkedList<>();
        subjects.addAll(pluginsLifecycleService.getAllPlugins());
        subjects.addAll(thingsLifecycleService.getAllThings());
        subjects.addAll(actuatorsLifecycleService.getAllActuators());
        return subjects;
    }
}
