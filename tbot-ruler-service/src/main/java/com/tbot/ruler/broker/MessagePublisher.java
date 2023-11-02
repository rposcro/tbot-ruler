package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.Message;

public interface MessagePublisher {

    void publishMessage(Message message);
}
