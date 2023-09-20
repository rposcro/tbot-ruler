package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessageDeliveryReport;

@FunctionalInterface
public interface MessageSender {

    void acceptDeliveryReport(MessageDeliveryReport deliveryReport);
}
