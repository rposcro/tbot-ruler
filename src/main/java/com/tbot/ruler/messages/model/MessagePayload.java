package com.tbot.ruler.messages.model;

import com.tbot.ruler.exceptions.MessageUnsupportedException;

public interface MessagePayload {

    default boolean is(Class<? extends MessagePayload> payloadClass) {
        return this.getClass().isAssignableFrom(payloadClass);
    }

    default <T extends MessagePayload> T ensureMessageType() {
        try {
            return (T) this;
        } catch(ClassCastException e) {
            throw new MessageUnsupportedException("Unsupported message type: " + this.getClass());
        }
    }
}
