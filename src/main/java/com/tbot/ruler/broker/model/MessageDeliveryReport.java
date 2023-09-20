package com.tbot.ruler.broker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Set;

@Getter
public class MessageDeliveryReport {

    private Message originalMessage;
    private Set<String> failedReceivers;
    private Set<String> successfulReceivers;
    private boolean senderSuspended;

    @Builder(builderClassName = "DeliveryReportBuilder")
    public MessageDeliveryReport(
        @NonNull @Singular Set<String> failedReceivers,
        @NonNull @Singular Set<String> successfulReceivers,
        @NonNull Message originalMessage,
        boolean senderSuspended) {
        this.failedReceivers = failedReceivers;
        this.successfulReceivers = successfulReceivers;
        this.originalMessage = originalMessage;
        this.senderSuspended = senderSuspended;
    }

    public boolean isSenderSuspended() {
        return senderSuspended;
    }

    public boolean noReceiversFound() {
        return !senderSuspended && failedReceivers.isEmpty() && successfulReceivers.isEmpty();
    }

    public boolean deliverySuccessful() {
        return !senderSuspended && failedReceivers.isEmpty() && !successfulReceivers.isEmpty();
    }

    public boolean deliveryFailed() {
        return !senderSuspended && !failedReceivers.isEmpty() && successfulReceivers.isEmpty();
    }

    public boolean deliveryPartiallyFailed() {
        return !senderSuspended && !failedReceivers.isEmpty() && !successfulReceivers.isEmpty();
    }
}
