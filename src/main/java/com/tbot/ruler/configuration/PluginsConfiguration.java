package com.tbot.ruler.configuration;

import com.tbot.ruler.service.things.ThingsLifetimeService;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Thing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class PluginsConfiguration {

    @Autowired
    private ThingsLifetimeService thingsLifetimeService;

    @Bean
    public List<Actuator> actuators() {
        return things().stream()
                .flatMap(thing -> thing.getActuators().stream())
                .collect(Collectors.toList());
    }

    @Bean
    public Map<String, Actuator> actuatorsPerId() {
        return actuators().stream().collect(Collectors.toMap(Actuator::getUuid, Function.identity()));
    }

    @Bean
    public List<Thing> things() {
        return thingsLifetimeService.getAllPlugins().stream()
                .flatMap(plugin -> plugin.getThings().stream())
                .collect(Collectors.toList());
    }
}
