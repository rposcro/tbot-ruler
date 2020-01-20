package com.tbot.ruler.message;

import com.tbot.ruler.things.ItemId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class Message {

    private final static AtomicLong idSequence = new AtomicLong(1);

    private final long id = idSequence.getAndIncrement();

    private ItemId senderId;
    private MessagePayload payload;

    @Builder
    public Message(@NonNull ItemId senderId, @NonNull MessagePayload payload) {
        this.senderId = senderId;
        this.payload = payload;
    }
}
