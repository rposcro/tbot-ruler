package com.tbot.ruler.messages.model;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class MessageDeliveryReportTest {

    @Test
    public void testSenderSuspended() {
        MessageDeliveryReport report = MessageDeliveryReport.builder()
                .originalMessage(mockMessage())
                .senderSuspended(true)
                .build();

        Assert.assertTrue(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertFalse(report.deliveryFailed());
        Assert.assertFalse(report.deliveryPartiallyFailed());
        Assert.assertFalse(report.deliverySuccessful());
    }

    @Test
    public void testNoReceiversFound() {
        MessageDeliveryReport report = MessageDeliveryReport.builder()
                .originalMessage(mockMessage())
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertTrue(report.noReceiversFound());
        Assert.assertFalse(report.deliveryFailed());
        Assert.assertFalse(report.deliveryPartiallyFailed());
        Assert.assertFalse(report.deliverySuccessful());
    }

    @Test
    public void testDeliverySuccessful() {
        MessageDeliveryReport report = MessageDeliveryReport.builder()
                .originalMessage(mockMessage())
                .successfulReceiver("success-receiver-id")
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertFalse(report.deliveryFailed());
        Assert.assertFalse(report.deliveryPartiallyFailed());
        Assert.assertTrue(report.deliverySuccessful());
    }

    @Test
    public void testDeliveryFailed() {
        MessageDeliveryReport report = MessageDeliveryReport.builder()
                .originalMessage(mockMessage())
                .failedReceiver("fail-receiver-id")
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertTrue(report.deliveryFailed());
        Assert.assertFalse(report.deliveryPartiallyFailed());
        Assert.assertFalse(report.deliverySuccessful());
    }

    @Test
    public void testDeliveryPartiallyFailed() {
        MessageDeliveryReport report = MessageDeliveryReport.builder()
                .originalMessage(mockMessage())
                .failedReceiver("fail-receiver-id")
                .successfulReceiver("success-receiver-id")
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertFalse(report.deliveryFailed());
        Assert.assertTrue(report.deliveryPartiallyFailed());
        Assert.assertFalse(report.deliverySuccessful());
    }

    private Message mockMessage() {
        return Message.builder()
                .senderId("sender-id")
                .payload("Payload")
                .build();
    }
}
