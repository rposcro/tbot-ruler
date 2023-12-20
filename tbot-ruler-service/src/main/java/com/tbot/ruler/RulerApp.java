package com.tbot.ruler;

import java.util.Optional;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Slf4j
@SpringBootApplication(scanBasePackages = { "com.tbot.ruler"})
@EnableWebSecurity
public class RulerApp implements CommandLineRunner {
    
    public static final String APPLICATION_NAME = "tbot-ruler-service";

    public static final String ACTIVE_PROFILES_ENV = "SPRING_PROFILES_ACTIVE";
    public static final String ACTIVE_PROFILES_SYS = "spring.profiles.active";
    public static final String DEFAULT_ACTIVE_PROFILES = "dev";

    @Override
    public void run(String... arg0) throws Exception {
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(RulerApp.class)
            .listeners(new RulerApplicationListener())
            .properties(getCustomProperties())
            .profiles(determineActiveProfiles())
            .run(args);
    }

    static Properties getCustomProperties() {
        Properties props = new Properties();
        props.put("spring.config.name", APPLICATION_NAME);
        return props;
    }

    private static String determineActiveProfiles() {
        return Optional.ofNullable(System.getenv(ACTIVE_PROFILES_ENV))
            .orElse(System.getProperty(ACTIVE_PROFILES_SYS, DEFAULT_ACTIVE_PROFILES));
    }

    private static void logProperty(String propertyName) {
        log.info("{}: {}", propertyName, System.getProperty(propertyName));
    }
}
