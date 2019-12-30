package com.tbot.ruler.configuration;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;

@Slf4j
@Configuration
public class EmittersConfiguration {

    @Bean
    public List<Emitter> emitters(List<Thing> things) {
        List<Emitter> emitters = things.stream()
            .filter(thing -> thing.getEmitters() != null)
            .flatMap(thing -> thing.getEmitters().stream())
            .collect(Collectors.toList());
        return emitters;
    }
}
