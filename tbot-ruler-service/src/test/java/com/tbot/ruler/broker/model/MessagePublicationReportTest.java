package com.tbot.ruler.broker.model;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class MessagePublicationReportTest {

    @Test
    public void testSenderSuspended() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .senderSuspended(true)
                .build();

        Assert.assertTrue(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertFalse(report.publicationFailed());
        Assert.assertFalse(report.publicationPartiallyFailed());
        Assert.assertFalse(report.publicationSuccessful());
    }

    @Test
    public void testNoReceiversFound() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertTrue(report.noReceiversFound());
        Assert.assertFalse(report.publicationFailed());
        Assert.assertFalse(report.publicationPartiallyFailed());
        Assert.assertFalse(report.publicationSuccessful());
    }

    @Test
    public void testDeliverySuccessful() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .successfulReceiver("success-receiver-id")
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertFalse(report.publicationFailed());
        Assert.assertFalse(report.publicationPartiallyFailed());
        Assert.assertTrue(report.publicationSuccessful());
    }

    @Test
    public void testDeliveryFailed() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .failedReceiver("fail-receiver-id")
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertTrue(report.publicationFailed());
        Assert.assertFalse(report.publicationPartiallyFailed());
        Assert.assertFalse(report.publicationSuccessful());
    }

    @Test
    public void testDeliveryPartiallyFailed() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .failedReceiver("fail-receiver-id")
                .successfulReceiver("success-receiver-id")
                .build();

        Assert.assertFalse(report.isSenderSuspended());
        Assert.assertFalse(report.noReceiversFound());
        Assert.assertFalse(report.publicationFailed());
        Assert.assertTrue(report.publicationPartiallyFailed());
        Assert.assertFalse(report.publicationSuccessful());
    }

    private Message mockMessage() {
        return Message.builder()
                .senderId("sender-id")
                .payload("Payload")
                .build();
    }
}
