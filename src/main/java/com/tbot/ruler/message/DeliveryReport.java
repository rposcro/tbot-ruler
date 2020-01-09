package com.tbot.ruler.message;

import com.tbot.ruler.things.ItemId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Set;

@Getter
public class DeliveryReport {

    private long relatedMessageId;
    private Set<ItemId> failedReceivers;
    private Set<ItemId> successfulReceivers;

    @Builder(builderClassName = "DeliveryReportBuilder")
    public DeliveryReport(
        @NonNull @Singular Set<ItemId> failedReceivers,
        @NonNull @Singular Set<ItemId> successfulReceivers,
        long relatedMessageId) {
        this.failedReceivers = failedReceivers;
        this.successfulReceivers = successfulReceivers;
        this.relatedMessageId = relatedMessageId;
    }

    public boolean noReceiversFound() {
        return failedReceivers.isEmpty() && successfulReceivers.isEmpty();
    }

    public boolean deliverySuccessful() {
        return failedReceivers.isEmpty() && !successfulReceivers.isEmpty();
    }

    public boolean deliveryFailed() {
        return !failedReceivers.isEmpty() && successfulReceivers.isEmpty();
    }

    public boolean deliveryPartiallyFailed() {
        return !failedReceivers.isEmpty() && !successfulReceivers.isEmpty();
    }
}
