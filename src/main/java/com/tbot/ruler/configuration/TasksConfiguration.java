package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.ApplianceStateRepository;
import com.tbot.ruler.persistance.json.JsonFileApplianceStateRepository;
import com.tbot.ruler.service.things.ThingsLifetimeService;
import com.tbot.ruler.things.TaskBasedItem;
import com.tbot.ruler.threads.EmissionTriggerContext;
import com.tbot.ruler.threads.Task;
import com.tbot.ruler.threads.TaskTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class TasksConfiguration {

    @Autowired
    private ThingsLifetimeService thingsLifetimeService;

    @Autowired
    private ApplianceStateRepository applianceStateRepository;

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler triggerableTasksScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        scheduler.afterPropertiesSet();
        return scheduler;
    }

    @Bean
    public ConcurrentTaskExecutor continuousTasksExecutor() {
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(Executors.newCachedThreadPool());
        return executor;
    }

    @Bean
    public ConcurrentTaskExecutor startUpTasksExecutor() {
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(Executors.newScheduledThreadPool(2));
        return executor;
    }

    @EventListener
    public void launchPersistenceFlushThread(ApplicationReadyEvent event) {
        if (applianceStateRepository instanceof JsonFileApplianceStateRepository) {
            triggerableTasksScheduler().schedule(
                    () -> ((JsonFileApplianceStateRepository) applianceStateRepository).flush(),
                    context -> new Date(Optional.ofNullable(context.lastCompletionTime()).orElse(new Date()).getTime() + 60_000)
            );
            log.info("Scheduled periodic persistence flush task");
        }
    }

    @EventListener
    public void launchTaskBasedItemsThreads(ApplicationReadyEvent event) {
        List<TaskBasedItem> taskBasedItems = taskBasedItems();
        taskBasedItems.stream()
                .flatMap(item -> item.getAsynchronousTasks().stream())
                .filter(Task::runOnStartUp)
                .map(Task::getRunnable)
                .forEach(runnable -> startUpTasksExecutor().execute(runnable));

        taskBasedItems.stream()
                .flatMap(item -> item.getAsynchronousTasks().stream())
                .filter(Task::runContinuously)
                .map(Task::getRunnable)
                .forEach(runnable -> continuousTasksExecutor().execute(runnable));

        taskBasedItems.stream()
                .flatMap(item -> item.getAsynchronousTasks().stream())
                .filter(Task::runOnTrigger)
                .forEach(task -> {
                    Runnable runnable = task.getRunnable();
                    TaskTrigger trigger = task.getTaskTrigger();
                    triggerableTasksScheduler().schedule(
                            runnable,
                            context -> {
                                Date nextEmissionTime = trigger.nextEmissionTime(new EmissionTriggerContext(context.lastScheduledExecutionTime()));
                                log.info("Next emission time for {} is {}", task, nextEmissionTime);
                                return nextEmissionTime;
                            });
                });
    }

    private List<TaskBasedItem> taskBasedItems() {
        LinkedList<TaskBasedItem> items = new LinkedList<>();
        items.addAll(thingsLifetimeService.getAllPlugins());
        items.addAll(thingsLifetimeService.getAllThings());
        items.addAll(thingsLifetimeService.getAllActuators());
        return items;
    }
}
