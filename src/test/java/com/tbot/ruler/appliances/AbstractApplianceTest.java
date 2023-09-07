package com.tbot.ruler.appliances;

import com.tbot.ruler.messages.model.Message;

import java.util.UUID;

public abstract class AbstractApplianceTest {

    protected Message mockMessage(Object payload) {
        return Message.builder()
                .senderId(UUID.randomUUID().toString())
                .payload(payload)
                .build();
    }
}
