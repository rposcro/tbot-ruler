package com.tbot.ruler.messages.model;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicLong;

public class Message {

    private final static AtomicLong idSequence = new AtomicLong(1);

    private final long id = idSequence.getAndIncrement();

    @Getter
    private String senderId;
    @Getter
    private MessagePayload payload;

    @Builder
    public Message(@NonNull String senderId, @NonNull MessagePayload payload) {
        this.senderId = senderId;
        this.payload = payload;
    }

    public <T> T getPayloadObject() {
        try {
            return (T) this;
        } catch(ClassCastException e) {
            throw new MessageUnsupportedException("Unsupported message type: " + this.getClass());
        }
    }
}
