package com.tbot.ruler.things;

import com.tbot.ruler.messages.MessageReceiver;
import com.tbot.ruler.messages.MessageSender;
import com.tbot.ruler.messages.model.MessageDeliveryReport;

public interface Actuator extends TaskBasedItem, MessageReceiver, MessageSender {

    default void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }
}
