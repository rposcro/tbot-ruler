package com.tbot.ruler.subjects;

import com.tbot.ruler.exceptions.MessageUnsupportedException;

public interface SubjectState {

    String getSubjectUuid();
    Object getPayload();

    default boolean isPayloadAs(Class<?> clazz) {
        return getPayload().getClass().isAssignableFrom(clazz);
    }

    default <T> T getPayloadAs(Class<T> clazz) {
        try {
            return clazz.cast(this.getPayload());
        } catch(ClassCastException e) {
            throw new MessageUnsupportedException(
                    String.format("Illegal payload type %s while expected %s", getPayload().getClass(), clazz));
        }
    }
}
