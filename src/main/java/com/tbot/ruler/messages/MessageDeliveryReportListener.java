package com.tbot.ruler.messages;

import com.tbot.ruler.messages.model.MessageDeliveryReport;

public interface MessageDeliveryReportListener {

    default void deliveryReportCompleted(MessageDeliveryReport deliveryReport) {
    }
}
