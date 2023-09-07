package com.tbot.ruler.configuration;

import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.Thing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class CollectorsConfiguration {

    @Autowired
    private List<Thing> things;

    @Bean
    public List<Collector> collectors() {
        List<Collector> collectors = things.stream()
                .filter(thing -> thing.getCollectors() != null)
                .flatMap(thing -> thing.getCollectors().stream())
                .collect(Collectors.toList());
        return collectors;
    }

    @Bean
    public Map<String, Collector> collectorsPerId() {
        return collectors().stream().collect(Collectors.toMap(Collector::getId, Function.identity()));
    }
}
