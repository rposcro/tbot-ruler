package com.tbot.ruler.configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;

@Slf4j
@Configuration
public class EmittersConfiguration {

    @Autowired
    private List<Thing> things;

    @Bean
    public List<Emitter> emitters() {
        List<Emitter> emitters = things.stream()
            .filter(thing -> thing.getEmitters() != null)
            .flatMap(thing -> thing.getEmitters().stream())
            .collect(Collectors.toList());
        return emitters;
    }

    @Bean
    public Map<String, Emitter> emittersPerId() {
        return emitters().stream().collect(Collectors.toMap(Emitter::getId, Function.identity()));
    }
}
