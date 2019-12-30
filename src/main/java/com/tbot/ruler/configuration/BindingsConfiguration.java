package com.tbot.ruler.configuration;

import java.util.*;
import java.util.stream.Collectors;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.things.*;
import com.tbot.ruler.things.service.MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BindingsConfiguration {

    @Value("${ruler.thingsConfig.path}")
    private String configPath;

    @Autowired
    private DTOConfiguration dtoConfiguration;
    @Autowired
    private  Map<ApplianceId, Appliance> appliancesPerId;
    @Autowired
    private Map<ActuatorId, Actuator> actuatorsPerId;
    @Autowired
    private Map<CollectorId, Collector> collectorsPerId;

    @Bean
    public Map<ItemId, List<ItemId>> consumerIdsBySenderId() {
        Map<ItemId, List<ItemId>> mappings = new HashMap<>();
        dtoConfiguration.bindingDTOs().stream()
            .forEach(bindingDTO -> {
                mappings.computeIfAbsent(bindingDTO.getSenderId(), senderId -> new LinkedList())
                    .addAll(bindingDTO.getConsumerIds());
            });
        log.debug(String.format("Item ids bindings: %s", mappings));
        return mappings;
    }

    @Bean
    public Map<ItemId, List<MessageConsumer>> consumersBySenderId() {
        Map<ItemId, List<MessageConsumer>> mappings = new HashMap<>();
        Map<ItemId, MessageConsumer> consumersPerId = new HashMap<>();
        consumersPerId.putAll(appliancesPerId);
        consumersPerId.putAll(actuatorsPerId);
        consumersPerId.putAll(collectorsPerId);

        consumerIdsBySenderId().entrySet().stream()
            .forEach(entry -> {
                mappings.put(entry.getKey(), entry.getValue().stream()
                        .map(consumersPerId::get)
                        .collect(Collectors.toList())
                );
            });
        log.debug(String.format("Message consumers bindings: %s", mappings));
        return mappings;
    }
}
