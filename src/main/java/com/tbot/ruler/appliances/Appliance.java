package com.tbot.ruler.appliances;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessagePayload;
import com.tbot.ruler.messages.MessageSender;
import com.tbot.ruler.things.Item;
import com.tbot.ruler.messages.MessageReceiver;

import java.util.Optional;

public interface Appliance<T> extends Item, MessageReceiver, MessageSender {

    Optional<Message> acceptDirectPayload(MessagePayload payload);
    Optional<T> getState();
    @Override
    String getUuid();
}
