package com.tbot.ruler.configuration;

import com.tbot.ruler.service.PersistenceService;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.TaskBasedItem;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.thread.EmissionTriggerContext;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class TasksConfiguration {

    @Autowired private List<Emitter> emitters;

    @Autowired private List<Actuator> actuators;

    @Autowired private List<Thing> things;

    @Autowired PersistenceService persistenceService;

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler periodicEmittersScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        scheduler.afterPropertiesSet();
        return scheduler;
    }

    @Bean
    public ConcurrentTaskExecutor continuousEmittersExecutor() {
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(Executors.newCachedThreadPool());
        return executor;
    }

    @Bean
    public ConcurrentTaskExecutor startUpExecutor() {
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(Executors.newScheduledThreadPool(2));
        return executor;
    }

    @EventListener
    public void launchPersistenceFlushThread(ApplicationReadyEvent event) {
        periodicEmittersScheduler().schedule(
            () -> persistenceService.flush(),
            context -> new Date(Optional.ofNullable(context.lastCompletionTime()).orElse(new Date()).getTime() + 60_000)
        );
        log.info("Scheduled periodic persistence flush task");
    }

    @EventListener
    public void launchTaskBasedItemThreads(ApplicationReadyEvent event) {
        List<TaskBasedItem> taskBasedItems = taskBasedItems();
        startUpTasks(taskBasedItems)
            .stream()
            .forEach(task -> startUpExecutor().execute(task));

        periodicItems(taskBasedItems).forEach(
            (item) -> {
                Runnable task = (Runnable) item.getTriggerableTask().get();
                TaskTrigger trigger = (TaskTrigger) item.getTaskTrigger().get();
                periodicEmittersScheduler().schedule(
                    task,
                    context -> {
                        Date nextEmissionTime = trigger.nextEmissionTime(new EmissionTriggerContext(context.lastScheduledExecutionTime()));
                        log.debug("Next emission time for {} is {}", task, nextEmissionTime);
                        return nextEmissionTime;
                    });
            }
        );

        continuousItems(taskBasedItems)
            .stream()
            .map(item -> (Runnable) item.getTriggerableTask().get())
            .forEach(task -> continuousEmittersExecutor().execute(task));
    }

    private List<TaskBasedItem> taskBasedItems() {
        ArrayList<TaskBasedItem> items = new ArrayList<>(emitters.size() + actuators.size() + things.size());
        items.addAll(emitters);
        items.addAll(actuators);
        items.addAll(things);
        return items;
    }

    private List<Runnable> startUpTasks(List<TaskBasedItem> items) {
        return items.stream()
            .filter(item -> item.getStartUpTask().isPresent())
            .map(item -> (Runnable) item.getStartUpTask().get())
            .collect(Collectors.toList());
    }

    private List<TaskBasedItem> periodicItems(List<TaskBasedItem> items) {
        return items.stream()
            .filter(item -> item.getTriggerableTask().isPresent() && item.getTaskTrigger().isPresent())
            .collect(Collectors.toList());
    }

    private List<TaskBasedItem> continuousItems(List<TaskBasedItem> items) {
        return items.stream()
            .filter(item -> item.getTriggerableTask().isPresent() && !item.getTaskTrigger().isPresent())
            .collect(Collectors.toList());
    }
}
