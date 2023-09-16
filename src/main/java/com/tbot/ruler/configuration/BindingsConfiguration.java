package com.tbot.ruler.configuration;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.messages.MessageSender;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.things.*;
import com.tbot.ruler.messages.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class BindingsConfiguration {

    @Autowired
    private BindingsRepository bindingsRepository;
    @Autowired
    private List<Appliance> appliances;
    @Autowired
    private List<Actuator> actuators;

    private Map<String, MessageReceiver> receiversPerId;
    private Map<String, MessageSender> sendersPerId;

    @PostConstruct
    public void init() {
        this.receiversPerId = new HashMap<>();
        receiversPerId.putAll(appliances.stream().collect(Collectors.toMap(Appliance::getUuid, Function.identity())));
        receiversPerId.putAll(actuators.stream().collect(Collectors.toMap(Actuator::getUuid, Function.identity())));

        this.sendersPerId = new HashMap<>();
        sendersPerId.putAll(appliances.stream().collect(Collectors.toMap(Appliance::getUuid, Function.identity())));
        sendersPerId.putAll(actuators.stream().collect(Collectors.toMap(Actuator::getUuid, Function.identity())));
    }

    @Bean
    public Map<String, List<String>> consumerIdsBySenderId() {
        Map<String, List<String>> mappings = new HashMap<>();
        bindingsRepository.findAll().stream()
                        .forEach(entity -> {
                            mappings.computeIfAbsent(entity.getSenderUuid(), senderId -> new LinkedList())
                                    .add(entity.getReceiverUuid());
                        });
        log.debug(String.format("Item ids bindings: %s", mappings));
        return mappings;
    }

    @Bean
    public Map<String, List<MessageReceiver>> consumersBySenderId() {
        Map<String, List<MessageReceiver>> mappings = new HashMap<>();

        consumerIdsBySenderId().entrySet().stream()
            .forEach(entry -> {
                mappings.put(entry.getKey(), entry.getValue().stream()
                        .map(receiversPerId::get)
                        .collect(Collectors.toList())
                );
            });
        log.debug(String.format("Message consumers bindings: %s", mappings));
        return mappings;
    }

    @Bean
    public Map<String, MessageReceiver> receiversPerId() {
        return this.receiversPerId;
    }

    @Bean
    public Map<String, MessageSender> sendersPerId() {
        return this.sendersPerId;
    }
}
