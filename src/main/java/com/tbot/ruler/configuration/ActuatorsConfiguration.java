package com.tbot.ruler.configuration;

import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class ActuatorsConfiguration {

    @Autowired
    private List<Thing> things;

    @Bean
    public List<Actuator> actuators() {
        List<Actuator> actuators = things.stream()
                .filter(thing -> thing.getActuators() != null)
                .flatMap(thing -> thing.getActuators().stream())
                .collect(Collectors.toList());
        return actuators;
    }

    @Bean
    public Map<String, Actuator> actuatorsPerId() {
        return actuators().stream().collect(Collectors.toMap(Actuator::getUuid, Function.identity()));
    }
}
