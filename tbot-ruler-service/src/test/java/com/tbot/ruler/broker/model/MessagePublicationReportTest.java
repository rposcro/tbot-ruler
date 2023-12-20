package com.tbot.ruler.broker.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessagePublicationReportTest {

    @Test
    public void testSenderSuspended() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .senderSuspended(true)
                .build();

        assertTrue(report.isSenderSuspended());
        assertFalse(report.noReceiversFound());
        assertFalse(report.publicationFailed());
        assertFalse(report.publicationPartiallyFailed());
        assertFalse(report.publicationSuccessful());
    }

    @Test
    public void testNoReceiversFound() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .build();

        assertFalse(report.isSenderSuspended());
        assertTrue(report.noReceiversFound());
        assertFalse(report.publicationFailed());
        assertFalse(report.publicationPartiallyFailed());
        assertFalse(report.publicationSuccessful());
    }

    @Test
    public void testDeliverySuccessful() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .successfulReceiver("success-receiver-id")
                .build();

        assertFalse(report.isSenderSuspended());
        assertFalse(report.noReceiversFound());
        assertFalse(report.publicationFailed());
        assertFalse(report.publicationPartiallyFailed());
        assertTrue(report.publicationSuccessful());
    }

    @Test
    public void testDeliveryFailed() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .failedReceiver("fail-receiver-id")
                .build();

        assertFalse(report.isSenderSuspended());
        assertFalse(report.noReceiversFound());
        assertTrue(report.publicationFailed());
        assertFalse(report.publicationPartiallyFailed());
        assertFalse(report.publicationSuccessful());
    }

    @Test
    public void testDeliveryPartiallyFailed() {
        MessagePublicationReport report = MessagePublicationReport.builder()
                .originalMessage(mockMessage())
                .failedReceiver("fail-receiver-id")
                .successfulReceiver("success-receiver-id")
                .build();

        assertFalse(report.isSenderSuspended());
        assertFalse(report.noReceiversFound());
        assertFalse(report.publicationFailed());
        assertTrue(report.publicationPartiallyFailed());
        assertFalse(report.publicationSuccessful());
    }

    private Message mockMessage() {
        return Message.builder()
                .senderId("sender-id")
                .payload("Payload")
                .build();
    }
}
