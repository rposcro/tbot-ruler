package com.tbot.ruler.service.things;

import com.tbot.ruler.configuration.BindingsConfiguration;
import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.messages.MessageSender;
import com.tbot.ruler.messages.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class BindingsService {

    @Autowired
    private BindingsConfiguration bindingsConfiguration;

    public Collection<MessageReceiver> findBindedMessageConsumers(String itemId) {
        return bindingsConfiguration.consumersBySenderId().getOrDefault(itemId, Collections.emptyList());
    }

    public Collection<String> findBindedMessageConsumerIds(String itemId) {
        return bindingsConfiguration.consumerIdsBySenderId().getOrDefault(itemId, Collections.emptyList());
    }

    public MessageReceiver messageReceiverById(String receiverId) {
        return bindingsConfiguration.receiversPerId().computeIfAbsent(
            receiverId,
            (itemId) -> { throw new ConfigurationException("No receiverId " + itemId + " found in configuration!"); });
    }

    public MessageSender messageSenderById(String senderId) {
        return bindingsConfiguration.sendersPerId().computeIfAbsent(
            senderId,
            (itemId) -> { throw new ConfigurationException("No senderId " + itemId + " found in configuration!"); });
    }
}
