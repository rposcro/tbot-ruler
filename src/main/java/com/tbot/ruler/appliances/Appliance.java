package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.state.State;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.things.ApplianceId;
import com.tbot.ruler.things.Item;
import com.tbot.ruler.things.service.MessageConsumer;

import java.util.Optional;

public interface Appliance<T extends State> extends Item<ApplianceId>, MessageConsumer {

    Optional<Message> acceptDirectPayload(MessagePayload payload);
    Optional<T> getState();
    @Override
    ApplianceId getId();
}
