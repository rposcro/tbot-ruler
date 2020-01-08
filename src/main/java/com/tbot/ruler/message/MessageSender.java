package com.tbot.ruler.message;

@FunctionalInterface
public interface MessageSender {

    void acceptDeliveryReport(DeliveryReport deliveryReport);
}
