package com.tbot.ruler.broker.model;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.atomic.AtomicLong;

@Getter
public class Message {

    private final static AtomicLong idSequence = new AtomicLong(1);

    private final long id = idSequence.getAndIncrement();

    private String senderId;
    private Object payload;

    @Builder
    public Message(@NonNull String senderId, @NonNull Object payload) {
        this.senderId = senderId;
        this.payload = payload;
    }

    public boolean isPayloadAs(Class<?> clazz) {
        return payload.getClass().isAssignableFrom(clazz);
    }

    public <T> T getPayloadAs(Class<T> clazz) {
        try {
            return clazz.cast(this.payload);
        } catch(ClassCastException e) {
            throw new MessageUnsupportedException(
                    String.format("Illegal payload type %s while expected %s", payload.getClass(), clazz));
        }
    }
}
