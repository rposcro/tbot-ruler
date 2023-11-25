package com.tbot.ruler.service.lifetime;

import com.tbot.ruler.subjects.Subject;
import com.tbot.ruler.task.EmissionTriggerContext;
import com.tbot.ruler.task.SubjectTask;
import com.tbot.ruler.task.TaskTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@Scope("singleton")
public class SubjectsTasksLifetimeService {

    @Autowired
    private SubjectLifetimeService subjectLifetimeService;
    @Autowired
    private ThreadPoolTaskScheduler triggerableTasksScheduler;
    @Autowired
    private ConcurrentTaskExecutor continuousTasksExecutor;
    @Autowired
    private ConcurrentTaskExecutor startUpTasksExecutor;

    @EventListener
    public void initialize(ApplicationReadyEvent event) {
        List<Subject> subjects = subjects();
        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousTasks().stream())
                .filter(SubjectTask::runsOnStartUp)
                .map(SubjectTask::getRunnable)
                .forEach(startUpTasksExecutor::execute);

        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousTasks().stream())
                .filter(SubjectTask::runsContinuously)
                .map(SubjectTask::getRunnable)
                .forEach(continuousTasksExecutor::execute);

        subjects.stream()
                .flatMap(subject -> subject.getAsynchronousTasks().stream())
                .filter(SubjectTask::runsOnTrigger)
                .forEach(task -> {
                    Runnable runnable = task.getRunnable();
                    TaskTrigger trigger = task.getTaskTrigger();
                    triggerableTasksScheduler.schedule(
                            runnable,
                            context -> {
                                Date nextEmissionTime = trigger.nextEmissionTime(new EmissionTriggerContext(context.lastScheduledExecutionTime()));
                                log.info("Next emission time for {} is {}", task, nextEmissionTime);
                                return nextEmissionTime.toInstant();
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
