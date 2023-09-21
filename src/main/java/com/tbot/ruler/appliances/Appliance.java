package com.tbot.ruler.appliances;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePayload;
import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.subjects.Subject;

import java.util.Optional;

public interface Appliance<T> extends Subject, MessageReceiver, MessageSender {

    Optional<Message> acceptDirectPayload(MessagePayload payload);
    Optional<T> getState();
}
