package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.ServiceTimeoutException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SynchronousMessagePublisherTest {

    @Mock
    private MessagePublisher messagePublisher;

    private SynchronousMessagePublisher synchronousMessagePublisher;

    @BeforeEach
    public void setUp() {
        this.synchronousMessagePublisher = SynchronousMessagePublisher.builder()
                .messagePublisher(messagePublisher)
                .build();
    }

    @Test
    public void successfullySendsMessageAndReceivesReport() {
        final Message message = Message.builder().senderId("sender-id").payload(Notification.HEARTBEAT).build();
        final MessagePublicationReport publicationReport = MessagePublicationReport.builder().originalMessage(message).build();

        Mockito.doAnswer(args -> {
            synchronousMessagePublisher.publicationReportDelivered(publicationReport);
            return null;
        }).when(messagePublisher).publishMessage(eq(message));

        MessagePublicationReport returnedpublicationReport = synchronousMessagePublisher.publishAndWaitForReport(message, 10);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messagePublisher, times(1)).publishMessage(messageCaptor.capture());

        assertEquals(1, messageCaptor.getAllValues().size());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(publicationReport, returnedpublicationReport);
    }

    @Test
    public void timesOutWhenSynchronousSend() {
        final Message message = Message.builder().senderId("sender-id").payload(Notification.HEARTBEAT).build();

        Mockito.doNothing().when(messagePublisher).publishMessage(eq(message));

        assertThrows(ServiceTimeoutException.class,
                () -> synchronousMessagePublisher.publishAndWaitForReport(message, 10));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messagePublisher, times(1)).publishMessage(messageCaptor.capture());
    }
}
