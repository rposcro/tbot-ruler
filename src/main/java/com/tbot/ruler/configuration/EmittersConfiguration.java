package com.tbot.ruler.configuration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.EmissionTriggerContext;
import com.tbot.ruler.things.service.impl.ThreadRegistrationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;

@Slf4j
@Configuration
public class EmittersConfiguration {

    @Autowired
    private ThreadRegistrationServiceImpl threadRegistrationService;

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler periodicEmittersScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
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
    public List<Emitter> emitters(List<Thing> things) {
        List<Emitter> emitters = things.stream()
            .filter(thing -> thing.getEmitters() != null)
            .flatMap(thing -> thing.getEmitters().stream())
            .collect(Collectors.toList());
        return emitters;
    }

    @EventListener
    public void launchEmitterThreads(ApplicationReadyEvent event) {
        threadRegistrationService.getStartUpTasks()
            .stream()
            .map(EmissionThread::getRunnable)
            .forEach(Runnable::run);

        threadRegistrationService.getPeriodicThreads().forEach(
            (thread, trigger) -> {
                periodicEmittersScheduler().schedule(
                    thread.getRunnable(),
                    context -> {
                        Date nextEmissionTime = trigger.nextEmissionTime(new EmissionTriggerContext(context.lastScheduledExecutionTime()));
                        log.debug("Next emission time for {} is {}", thread, nextEmissionTime);
                        return nextEmissionTime;
                    });
            }
        );

        threadRegistrationService.getContinuousThreads()
            .stream()
            .map(EmissionThread::getRunnable)
            .forEach(continuousEmittersExecutor()::execute);
    }
}
