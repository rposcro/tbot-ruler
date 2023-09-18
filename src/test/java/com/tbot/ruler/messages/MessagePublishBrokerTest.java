package com.tbot.ruler.messages;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.Notification;
import com.tbot.ruler.service.things.BindingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessagePublishBrokerTest {

    @Mock
    private BindingsService bindingsService;

    @Mock
    private MessagePublicationManager messagePublicationManager;

    private MessageQueueComponent messageQueueComponent;

    private MessagePublishBroker messagePublishBroker;

    private ExecutorService brokerExecutorService;

    @BeforeEach
    public void setUp() {
        this.messageQueueComponent = new MessageQueueComponent(2);
        this.messagePublishBroker = MessagePublishBroker.builder()
                .messageQueue(messageQueueComponent)
                .bindingsService(bindingsService)
                .messagePublicationManager(messagePublicationManager)
                .build();

        this.brokerExecutorService = Executors.newSingleThreadExecutor();
        this.brokerExecutorService.execute(messagePublishBroker);
    }

    @AfterEach
    public void tidyUp() {
        brokerExecutorService.shutdownNow();
    }

    @Test
    public void successfullyPublishesEnqueuedMessage() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();
        final MessageReceiver receiver = mock(MessageReceiver.class);

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId)))
                .thenReturn(Stream.of("receiver-1", "receiver-2").collect(Collectors.toList()));
        when(bindingsService.findReceiverByUuid(eq("receiver-1"))).thenReturn(receiver);
        when(bindingsService.findReceiverByUuid(eq("receiver-2"))).thenReturn(receiver);

        messageQueueComponent.enqueueMessage(message);
        MessageDeliveryReport deliveryReport = messageQueueComponent.nextDeliveryReport(100);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(receiver, times(2)).acceptMessage(messageCaptor.capture());

        assertEquals(2, messageCaptor.getAllValues().size());
        assertEquals(message, messageCaptor.getAllValues().get(0));
        assertEquals(message, messageCaptor.getAllValues().get(1));

        assertEquals(message, deliveryReport.getOriginalMessage());
        assertFalse(deliveryReport.isSenderSuspended());
        assertTrue(deliveryReport.deliverySuccessful());
        assertFalse(deliveryReport.deliveryFailed());
        assertFalse(deliveryReport.deliveryPartiallyFailed());
        assertFalse(deliveryReport.noReceiversFound());
    }

    @Test
    public void deliveryReportFailedWhenAllReceiversFail() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();
        final MessageReceiver receiver = Mockito.mock(MessageReceiver.class);

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId)))
                .thenReturn(Stream.of("receiver-1", "receiver-2").collect(Collectors.toList()));
        when(bindingsService.findReceiverByUuid(eq("receiver-1"))).thenReturn(receiver);
        when(bindingsService.findReceiverByUuid(eq("receiver-2"))).thenReturn(receiver);
        doThrow(new MessageException("terefere")).when(receiver).acceptMessage(eq(message));

        messageQueueComponent.enqueueMessage(message);
        MessageDeliveryReport deliveryReport = messageQueueComponent.nextDeliveryReport(100);

        verify(receiver, times(2)).acceptMessage(eq(message));

        assertEquals(message, deliveryReport.getOriginalMessage());
        assertFalse(deliveryReport.isSenderSuspended());
        assertFalse(deliveryReport.deliverySuccessful());
        assertTrue(deliveryReport.deliveryFailed());
        assertFalse(deliveryReport.deliveryPartiallyFailed());
        assertFalse(deliveryReport.noReceiversFound());
    }

    @Test
    public void deliveryReportPartiallyFailedWhenSomeReceiversFail() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();
        final MessageReceiver receiver = Mockito.mock(MessageReceiver.class);

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId)))
                .thenReturn(Stream.of("receiver-1", "receiver-2").collect(Collectors.toList()));
        when(bindingsService.findReceiverByUuid(eq("receiver-1"))).thenReturn(receiver);
        when(bindingsService.findReceiverByUuid(eq("receiver-2"))).thenReturn(receiver);

        doNothing().doThrow(new MessageException("terefere")).when(receiver).acceptMessage(eq(message));

        messageQueueComponent.enqueueMessage(message);
        MessageDeliveryReport deliveryReport = messageQueueComponent.nextDeliveryReport(100);

        verify(receiver, times(2)).acceptMessage(eq(message));

        assertEquals(message, deliveryReport.getOriginalMessage());
        assertFalse(deliveryReport.isSenderSuspended());
        assertFalse(deliveryReport.deliverySuccessful());
        assertFalse(deliveryReport.deliveryFailed());
        assertTrue(deliveryReport.deliveryPartiallyFailed());
        assertFalse(deliveryReport.noReceiversFound());
    }

    @Test
    public void deliveryReportWhenNoReceiversFound() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId))).thenReturn(Collections.emptyList());

        messageQueueComponent.enqueueMessage(message);
        MessageDeliveryReport deliveryReport = messageQueueComponent.nextDeliveryReport(100);

        assertEquals(message, deliveryReport.getOriginalMessage());
        assertFalse(deliveryReport.isSenderSuspended());
        assertFalse(deliveryReport.deliverySuccessful());
        assertFalse(deliveryReport.deliveryFailed());
        assertFalse(deliveryReport.deliveryPartiallyFailed());
        assertTrue(deliveryReport.noReceiversFound());
    }

    @Test
    public void deliveryReportWhenSenderSuspended() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId))).thenReturn(Collections.emptyList());
        when(messagePublicationManager.isSenderSuspended(eq(senderId))).thenReturn(true);

        messageQueueComponent.enqueueMessage(message);
        MessageDeliveryReport deliveryReport = messageQueueComponent.nextDeliveryReport(100);

        assertEquals(message, deliveryReport.getOriginalMessage());
        assertTrue(deliveryReport.isSenderSuspended());
        assertFalse(deliveryReport.deliverySuccessful());
        assertFalse(deliveryReport.deliveryFailed());
        assertFalse(deliveryReport.deliveryPartiallyFailed());
        assertFalse(deliveryReport.noReceiversFound());
    }
}
