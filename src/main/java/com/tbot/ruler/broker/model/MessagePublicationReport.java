package com.tbot.ruler.broker.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Set;

@Getter
public class MessagePublicationReport {

    private Message originalMessage;
    private Set<String> failedReceivers;
    private Set<String> successfulReceivers;
    private boolean senderSuspended;

    @Builder(builderClassName = "publicationReportBuilder")
    public MessagePublicationReport(
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

    public boolean publicationSuccessful() {
        return !senderSuspended && failedReceivers.isEmpty() && !successfulReceivers.isEmpty();
    }

    public boolean publicationFailed() {
        return !senderSuspended && !failedReceivers.isEmpty() && successfulReceivers.isEmpty();
    }

    public boolean publicationPartiallyFailed() {
        return !senderSuspended && !failedReceivers.isEmpty() && !successfulReceivers.isEmpty();
    }
}
