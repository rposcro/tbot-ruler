package com.tbot.ruler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

@Slf4j
public class RulerApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        environment.getSystemProperties().forEach((key, value) -> log.debug("{}: {}", key, value));
        environment.getPropertySources().stream()
            .filter(ps -> ps instanceof MapPropertySource)
            .map(ps -> ((MapPropertySource) ps))
            .forEach(this::logPropertySource);
    }

    private void logPropertySource(MapPropertySource source) {
        StringBuffer logLine = new StringBuffer("Properties from " + source.getName() + ":");
        source.getSource().forEach((key, value) -> logLine.append("\t" + key + ":" + value + "\n"));
        log.debug(logLine.toString());
    }
}
