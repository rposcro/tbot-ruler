package com.tbot.ruler.plugins.jwavez.actuators.notification;

import com.rposcro.jwavez.core.commands.supported.notification.NotificationReport;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.subjects.AbstractActuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
@Getter
public class NotificationActuator extends AbstractActuator {

    private MessagePublisher messagePublisher;
    private NotificationConfiguration configuration;

    @Builder
    public NotificationActuator(
        @NonNull String id,
        @NonNull String name,
        String description,
        @NonNull MessagePublisher messagePublisher,
        @NonNull NotificationConfiguration configuration
    ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.configuration = configuration;
    }

    public void acceptNotification(NotificationReport notificationReport) {
        int event = notificationReport.getNotificationEvent();
        OnOffState payload;

        if (isOnEvent(event)) {
            payload = OnOffState.STATE_ON;
        } else if (isOffEvent(event)) {
            payload = OnOffState.STATE_OFF;
        } else {
            log.info("Unsupported notification event {}. Skipped.", event);
            return;
        }

        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(payload)
                .build());
    }

    private boolean isOnEvent(int notificationEvent) {
        return IntStream.of(configuration.getOnEvents()).anyMatch(i -> i == notificationEvent);
    }

    private boolean isOffEvent(int notificationEvent) {
        return IntStream.of(configuration.getOffEvents()).anyMatch(i -> i == notificationEvent);
    }
}
