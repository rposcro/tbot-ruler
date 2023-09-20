package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.ApplianceStateRepository;
import com.tbot.ruler.persistance.json.JsonFileApplianceStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class TasksConfiguration {

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
}
