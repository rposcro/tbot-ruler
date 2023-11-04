package com.tbot.ruler.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class TasksConfiguration {

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
}
