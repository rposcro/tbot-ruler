package com.tbot.ruler.things;

import com.tbot.ruler.message.Message;

public interface Collector extends Item {

    void acceptMessage(Message message);
}
