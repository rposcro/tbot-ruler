package com.tbot.ruler.console;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Optional;
import java.util.Properties;

@SpringBootApplication
@ConfigurationPropertiesScan("com.tbot.ruler.console")
public class TBotRulerConsoleApplication {

 	public static final String APPLICATION_NAME = "tbot-ruler-console";

	public static final String ACTIVE_PROFILES_ENV = "SPRING_PROFILES_ACTIVE";
	public static final String ACTIVE_PROFILES_SYS = "spring.profiles.active";
	public static final String DEFAULT_ACTIVE_PROFILES = "dev";

	public static void main(String[] args) {
		new SpringApplicationBuilder(TBotRulerConsoleApplication.class)
				.properties(getCustomProperties())
				.profiles(determineActiveProfiles())
				.run(args);
	}

	static Properties getCustomProperties() {
		Properties props = new Properties();
		props.put("spring.config.name", APPLICATION_NAME);
		return props;
	}

	static String determineActiveProfiles() {
		return Optional.ofNullable(System.getenv(ACTIVE_PROFILES_ENV))
				.orElse(System.getProperty(ACTIVE_PROFILES_SYS, DEFAULT_ACTIVE_PROFILES));
	}
}
