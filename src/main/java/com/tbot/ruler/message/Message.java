package com.tbot.ruler.message;

import com.tbot.ruler.things.ItemId;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Message {

    private ItemId senderId;
    private MessagePayload payload;
}
