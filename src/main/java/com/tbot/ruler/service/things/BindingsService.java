package com.tbot.ruler.service.things;

import com.tbot.ruler.configuration.BindingsConfiguration;
import com.tbot.ruler.things.*;
import com.tbot.ruler.things.service.MessageConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class BindingsService {

    @Autowired
    private BindingsConfiguration bindingsConfiguration;

    public Collection<MessageConsumer> findBindedMessageConsumers(ItemId itemId) {
        return bindingsConfiguration.consumersBySenderId().getOrDefault(itemId, Collections.emptyList());
    }
}
