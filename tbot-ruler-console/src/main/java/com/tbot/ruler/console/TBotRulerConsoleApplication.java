package com.tbot.ruler.console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.tbot.ruler.console")
public class TBotRulerConsoleApplication {

	public static void main(String[] args) {
		SpringApplication.run(TBotRulerConsoleApplication.class, args);
	}

}
