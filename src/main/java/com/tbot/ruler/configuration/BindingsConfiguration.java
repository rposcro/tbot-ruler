package com.tbot.ruler.configuration;

import java.util.*;
import java.util.stream.Collectors;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.messages.MessageSender;
import com.tbot.ruler.things.*;
import com.tbot.ruler.messages.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class BindingsConfiguration {

    @Value("${ruler.thingsConfig.path}")
    private String configPath;

    @Autowired
    private DTOConfiguration dtoConfiguration;
    @Autowired
    private  Map<String, Appliance> appliancesPerId;
    @Autowired
    private Map<String, Actuator> actuatorsPerId;

    private Map<String, MessageReceiver> receiversPerId;
    private Map<String, MessageSender> sendersPerId;

    @PostConstruct
    public void init() {
        Map<String, MessageReceiver> receiverMap = new HashMap<>();
        receiverMap.putAll(appliancesPerId);
        receiverMap.putAll(actuatorsPerId);
        this.receiversPerId = receiverMap;

        Map<String, MessageSender> senderMap = new HashMap<>();
        senderMap.putAll(appliancesPerId);
        senderMap.putAll(actuatorsPerId);
        this.sendersPerId = senderMap;
    }

    @Bean
    public Map<String, List<String>> consumerIdsBySenderId() {
        Map<String, List<String>> mappings = new HashMap<>();
        dtoConfiguration.bindingDTOs().stream()
            .forEach(bindingDTO -> {
                mappings.computeIfAbsent(bindingDTO.getSenderId(), senderId -> new LinkedList())
                    .addAll(bindingDTO.getConsumerIds());
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
