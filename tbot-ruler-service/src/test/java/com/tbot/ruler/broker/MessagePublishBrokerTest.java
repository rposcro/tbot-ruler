package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.Notification;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.jobs.JobRunner;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        this.brokerExecutorService.execute(new JobRunner(JobBundle.continuousJobBundle(messagePublishBroker)));
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
        MessagePublicationReport publicationReport = messageQueueComponent.nextReport(100);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(receiver, times(2)).acceptMessage(messageCaptor.capture());

        assertEquals(2, messageCaptor.getAllValues().size());
        assertEquals(message, messageCaptor.getAllValues().get(0));
        assertEquals(message, messageCaptor.getAllValues().get(1));

        assertEquals(message, publicationReport.getOriginalMessage());
        assertFalse(publicationReport.isSenderSuspended());
        assertTrue(publicationReport.publicationSuccessful());
        assertFalse(publicationReport.publicationFailed());
        assertFalse(publicationReport.publicationPartiallyFailed());
        assertFalse(publicationReport.noReceiversFound());
    }

    @Test
    public void publicationReportFailedWhenAllReceiversFail() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();
        final MessageReceiver receiver = Mockito.mock(MessageReceiver.class);

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId)))
                .thenReturn(Stream.of("receiver-1", "receiver-2").collect(Collectors.toList()));
        when(bindingsService.findReceiverByUuid(eq("receiver-1"))).thenReturn(receiver);
        when(bindingsService.findReceiverByUuid(eq("receiver-2"))).thenReturn(receiver);
        doThrow(new MessageException("terefere")).when(receiver).acceptMessage(eq(message));

        messageQueueComponent.enqueueMessage(message);
        MessagePublicationReport publicationReport = messageQueueComponent.nextReport(100);

        verify(receiver, times(2)).acceptMessage(eq(message));

        assertEquals(message, publicationReport.getOriginalMessage());
        assertFalse(publicationReport.isSenderSuspended());
        assertFalse(publicationReport.publicationSuccessful());
        assertTrue(publicationReport.publicationFailed());
        assertFalse(publicationReport.publicationPartiallyFailed());
        assertFalse(publicationReport.noReceiversFound());
    }

    @Test
    public void publicationReportPartiallyFailedWhenSomeReceiversFail() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();
        final MessageReceiver receiver = Mockito.mock(MessageReceiver.class);

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId)))
                .thenReturn(Stream.of("receiver-1", "receiver-2").collect(Collectors.toList()));
        when(bindingsService.findReceiverByUuid(eq("receiver-1"))).thenReturn(receiver);
        when(bindingsService.findReceiverByUuid(eq("receiver-2"))).thenReturn(receiver);

        doNothing().doThrow(new MessageException("terefere")).when(receiver).acceptMessage(eq(message));

        messageQueueComponent.enqueueMessage(message);
        MessagePublicationReport publicationReport = messageQueueComponent.nextReport(100);

        verify(receiver, times(2)).acceptMessage(eq(message));

        assertEquals(message, publicationReport.getOriginalMessage());
        assertFalse(publicationReport.isSenderSuspended());
        assertFalse(publicationReport.publicationSuccessful());
        assertFalse(publicationReport.publicationFailed());
        assertTrue(publicationReport.publicationPartiallyFailed());
        assertFalse(publicationReport.noReceiversFound());
    }

    @Test
    public void publicationReportWhenNoReceiversFound() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId))).thenReturn(Collections.emptyList());

        messageQueueComponent.enqueueMessage(message);
        MessagePublicationReport publicationReport = messageQueueComponent.nextReport(100);

        assertEquals(message, publicationReport.getOriginalMessage());
        assertFalse(publicationReport.isSenderSuspended());
        assertFalse(publicationReport.publicationSuccessful());
        assertFalse(publicationReport.publicationFailed());
        assertFalse(publicationReport.publicationPartiallyFailed());
        assertTrue(publicationReport.noReceiversFound());
    }

    @Test
    public void publicationReportWhenSenderSuspended() throws Exception {
        final String senderId = "mocked-sender-id";
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();

        when(bindingsService.findReceiversUuidsBySenderUuid(eq(senderId))).thenReturn(Collections.emptyList());
        when(messagePublicationManager.isSenderSuspended(eq(senderId))).thenReturn(true);

        messageQueueComponent.enqueueMessage(message);
        MessagePublicationReport publicationReport = messageQueueComponent.nextReport(100);

        assertEquals(message, publicationReport.getOriginalMessage());
        assertTrue(publicationReport.isSenderSuspended());
        assertFalse(publicationReport.publicationSuccessful());
        assertFalse(publicationReport.publicationFailed());
        assertFalse(publicationReport.publicationPartiallyFailed());
        assertFalse(publicationReport.noReceiversFound());
    }
}
