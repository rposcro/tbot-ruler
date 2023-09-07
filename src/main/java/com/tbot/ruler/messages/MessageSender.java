package com.tbot.ruler.messages;

import com.tbot.ruler.messages.model.MessageDeliveryReport;

@FunctionalInterface
public interface MessageSender {

    void acceptDeliveryReport(MessageDeliveryReport deliveryReport);
}
