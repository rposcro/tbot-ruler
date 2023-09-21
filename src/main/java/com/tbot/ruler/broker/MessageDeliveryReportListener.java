package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.MessageDeliveryReport;

public interface MessageDeliveryReportListener {

    default void deliveryReportCompleted(MessageDeliveryReport deliveryReport) {
    }

    default void deliveryReportSkipped(MessageDeliveryReport deliveryReport) {
    }
}
