package com.tbot.ruler.broker.session;

import com.tbot.ruler.broker.model.Message;

public final class MessageSessionManager {

    private static ThreadLocal<MessageSessionContext> sessionContext = new ThreadLocal<>();

    public static MessageSessionContext getContext() {
        return sessionContext.get();
    }

    public static void setContext(MessageSessionContext context) {
        sessionContext.set(context);
    }

    public static void setContext(Message message, String receiverId) {
        MessageSessionContext context = MessageSessionContext.builder()
                .messageId(message.getId())
                .senderId(message.getSenderId())
                .receiverId(receiverId)
                .build();
        setContext(context);
    }

    public static MessageSessionContext removeContext() {
        MessageSessionContext context = sessionContext.get();
        sessionContext.remove();
        return context;
    }
}
