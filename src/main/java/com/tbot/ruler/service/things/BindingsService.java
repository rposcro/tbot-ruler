package com.tbot.ruler.service.things;

import com.tbot.ruler.configuration.BindingsConfiguration;
import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.message.MessageSender;
import com.tbot.ruler.things.*;
import com.tbot.ruler.message.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class BindingsService {

    @Autowired
    private BindingsConfiguration bindingsConfiguration;

    public Collection<MessageReceiver> findBindedMessageConsumers(ItemId itemId) {
        return bindingsConfiguration.consumersBySenderId().getOrDefault(itemId, Collections.emptyList());
    }

    public Collection<ItemId> findBindedMessageConsumerIds(ItemId itemId) {
        return bindingsConfiguration.consumerIdsBySenderId().getOrDefault(itemId, Collections.emptyList());
    }

    public MessageReceiver messageReceiverById(ItemId receiverId) {
        return bindingsConfiguration.receiversPerId().computeIfAbsent(
            receiverId,
            (itemId) -> { throw new ConfigurationException("No receiverId " + itemId.getValue() + " found in configuration!"); });
    }

    public MessageSender messageSenderById(ItemId senderId) {
        return bindingsConfiguration.sendersPerId().computeIfAbsent(
            senderId,
            (itemId) -> { throw new ConfigurationException("No senderId " + itemId.getValue() + " found in configuration!"); });
    }
}
