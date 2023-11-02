package com.tbot.ruler.broker.model;

import com.tbot.ruler.exceptions.MessageUnsupportedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessagePayload {

    private Object payload;

    public <T> T getPayloadAs(Class<T> clazz) {
        try {
            return clazz.cast(this.payload);
        } catch(ClassCastException e) {
            throw new MessageUnsupportedException(
                    String.format("Illegal payload type %s while expected %s", payload.getClass(), clazz));
        }
    }
}
