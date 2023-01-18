package com.tbot.ruler.messages;

import com.tbot.ruler.exceptions.ServiceTimeoutException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SynchronousMessageSenderTest {

    @Mock
    private MessagePublisher messagePublisher;

    private SynchronousMessageSender synchronousMessageSender;

    @BeforeEach
    public void setUp() {
        this.synchronousMessageSender = SynchronousMessageSender.builder()
                .messagePublisher(messagePublisher)
                .build();
    }

    @Test
    public void successfullySendsMessageAndReceivesReport() {
        final Message message = Message.builder().senderId("sender-id").payload(Notification.HEARTBEAT).build();
        final MessageDeliveryReport deliveryReport = MessageDeliveryReport.builder().originalMessage(message).build();

        Mockito.doAnswer(args -> {
            synchronousMessageSender.deliveryReportCompleted(deliveryReport);
            return null;
        }).when(messagePublisher).publishMessage(eq(message));

        MessageDeliveryReport returnedDeliveryReport = synchronousMessageSender.sendAndWaitForReport(message, 10);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messagePublisher, times(1)).publishMessage(messageCaptor.capture());

        assertEquals(1, messageCaptor.getAllValues().size());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(deliveryReport, returnedDeliveryReport);
    }

    @Test
    public void timesOutWhenSynchronousSend() {
        final Message message = Message.builder().senderId("sender-id").payload(Notification.HEARTBEAT).build();

        Mockito.doNothing().when(messagePublisher).publishMessage(eq(message));

        assertThrows(ServiceTimeoutException.class,
                () -> synchronousMessageSender.sendAndWaitForReport(message, 10));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messagePublisher, times(1)).publishMessage(messageCaptor.capture());
    }
}
