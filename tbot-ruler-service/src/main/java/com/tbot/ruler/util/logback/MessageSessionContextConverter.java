package com.tbot.ruler.util.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tbot.ruler.broker.session.MessageSessionContext;
import com.tbot.ruler.broker.session.MessageSessionManager;

public class MessageSessionContextConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        MessageSessionContext context = MessageSessionManager.getContext();

        if (context != null) {
            StringBuilder builder = new StringBuilder()
                .append("Msg<id: ").append(context.getMessageId())
                .append(", from: ").append(context.getSenderId());

            if (context.getReceiverId() != null) {
                builder.append(", to: ").append(context.getReceiverId());
            }

            builder.append("> - ");
            return builder.toString();
        }

        return "";
    }
}
