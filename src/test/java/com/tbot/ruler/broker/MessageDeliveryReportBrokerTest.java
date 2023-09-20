package com.tbot.ruler.broker;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.payload.Notification;
import com.tbot.ruler.service.things.BindingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageDeliveryReportBrokerTest {

    @Mock
    private BindingsService bindingsService;

    @Mock
    private MessageDeliveryReportListener messageDeliveryListener;

    private MessageQueueComponent messageQueueComponent;

    private MessageDeliveryReportBroker messageDeliveryBroker;

    private ExecutorService brokerExecutorService;

    @BeforeEach
    public void setUp() {
        this.messageQueueComponent = new MessageQueueComponent(2);
        this.messageDeliveryBroker = MessageDeliveryReportBroker.builder()
                .messageQueue(messageQueueComponent)
                .bindingsService(bindingsService)
                .deliveryListener(messageDeliveryListener)
                .build();

        this.brokerExecutorService = Executors.newSingleThreadExecutor();
        this.brokerExecutorService.execute(messageDeliveryBroker);
    }

    @AfterEach
    public void tidyUp() {
        brokerExecutorService.shutdownNow();
    }

    @Test
    public void successfullyDeliversEnqueuedReport() {
        final String senderId = "mocked-sender-id";
        final MessageSender sender = mock(MessageSender.class);
        final Message message = Message.builder().senderId(senderId).payload(Notification.HEARTBEAT).build();
        final MessageDeliveryReport report = MessageDeliveryReport.builder().originalMessage(message).build();

        when(bindingsService.findSenderByUuid(eq(senderId))).thenReturn(sender);

        messageQueueComponent.enqueueDeliveryReport(report);

        ArgumentCaptor<MessageDeliveryReport> senderReportCaptor = ArgumentCaptor.forClass(MessageDeliveryReport.class);
        verify(sender, timeout(100).times(1)).acceptDeliveryReport(senderReportCaptor.capture());
        ArgumentCaptor<MessageDeliveryReport> listenerReportCaptor = ArgumentCaptor.forClass(MessageDeliveryReport.class);
        verify(sender, timeout(100).times(1)).acceptDeliveryReport(listenerReportCaptor.capture());

        assertEquals(1, senderReportCaptor.getAllValues().size());
        assertEquals(report, senderReportCaptor.getValue());
        assertEquals(1, listenerReportCaptor.getAllValues().size());
        assertEquals(report, listenerReportCaptor.getValue());
    }
}
