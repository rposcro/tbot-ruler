package com.tbot.ruler.appliances;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.MessageSender;
import com.tbot.ruler.things.ApplianceId;
import com.tbot.ruler.things.Item;
import com.tbot.ruler.message.MessageReceiver;

import java.util.Optional;

public interface Appliance<T> extends Item<ApplianceId>, MessageReceiver, MessageSender {

    Optional<Message> acceptDirectPayload(MessagePayload payload);
    Optional<T> getState();
    @Override
    ApplianceId getId();
}
