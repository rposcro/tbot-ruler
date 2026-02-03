package com.tbot.ruler.broker.session;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MessageSessionContext {

    private long messageId;
    private String senderId;
    private String receiverId;
}
