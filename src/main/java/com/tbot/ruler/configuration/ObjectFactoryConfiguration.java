package com.tbot.ruler.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectFactoryConfiguration {

    @Bean
    public ParameterNamesModule parameterNamesModule() {
        return new ParameterNamesModule(JsonCreator.Mode.PROPERTIES);
    }

    @Bean
    public Jdk8Module jdk8Module() {
        return new Jdk8Module();
    }
}
