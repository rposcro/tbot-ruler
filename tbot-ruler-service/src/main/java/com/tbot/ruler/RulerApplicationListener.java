package com.tbot.ruler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class RulerApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        logProperty(environment, "spring.config.location");
        logProperty(environment, "spring.config.name");
        logProperty(environment, "spring.thymeleaf.prefix");
        logProperty(environment, "spring.thymeleaf.suffix");
        logProperty(environment, "spring.thymeleaf.mode");
        logProperty(environment, "spring.thymeleaf.servlet.content-type");
        logProperty(environment, "ruler.jsonRepository.path");
    }

    private static void logProperty(ConfigurableEnvironment environment, String propertyName) {
        log.info("{}: {}", propertyName, environment.getProperty(propertyName));
    }
}
