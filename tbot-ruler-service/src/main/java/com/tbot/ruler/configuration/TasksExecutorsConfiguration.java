package com.tbot.ruler.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;

@Slf4j
@Configuration
public class TasksExecutorsConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor rulerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setDaemon(true);
        executor.setAllowCoreThreadTimeOut(false);
        executor.initialize();
        return executor;
    }

    @Bean
    public ConcurrentTaskExecutor jobRunnersExecutor() {
        ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(Executors.newCachedThreadPool());
        return executor;
    }
}
