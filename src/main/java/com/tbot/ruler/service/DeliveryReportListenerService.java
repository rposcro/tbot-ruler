package com.tbot.ruler.service;

import com.tbot.ruler.exceptions.ServiceException;
import com.tbot.ruler.message.DeliveryReport;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class DeliveryReportListenerService {

    private final static long DEFAULT_TIMEOUT = 60_000;

    private Map<Long, Object> taskMap = new ConcurrentHashMap<>();

    public DeliveryReport deliverAndWaitForReport(long originalMessageId, Runnable deliveryTask) {
        return deliverAndWaitForReport(originalMessageId, deliveryTask, DEFAULT_TIMEOUT);
    }

    public DeliveryReport deliverAndWaitForReport(long originalMessageId, Runnable deliveryTask, long timeout) {
        CompletableFuture<DeliveryReport> future = settleFuture(originalMessageId);
        try {
            deliveryTask.run();
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch(Exception e) {
            removeFuture(originalMessageId, future);
            throw new ServiceException("Delivery report could not be received", e);
        }
    }

    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
        Object mapped = taskMap.remove(deliveryReport.getRelatedMessageId());
        if (mapped instanceof CompletableFuture) {
            deliverReport((CompletableFuture) mapped, deliveryReport);
        } else if (mapped instanceof Set) {
            ((Set<CompletableFuture>) mapped).stream()
                .forEach(future -> deliverReport(future, deliveryReport));
        }
    }

    private void deliverReport(CompletableFuture<DeliveryReport> future, DeliveryReport deliveryReport) {
        future.complete(deliveryReport);
    }

    private void removeFuture(Long originalMessageId, CompletableFuture<DeliveryReport> future) {
        taskMap.computeIfPresent(originalMessageId, (key, mapping) -> {
            if (mapping instanceof Set) {
                Set<CompletableFuture<DeliveryReport>> futureSet = (Set<CompletableFuture<DeliveryReport>>) mapping;
                futureSet.remove(future);
                if (!futureSet.isEmpty()) {
                    return futureSet;
                }
            }
            return null;
        });
    }

    private CompletableFuture<DeliveryReport> settleFuture(long originalMessageId) {
        CompletableFuture<DeliveryReport> future = new CompletableFuture<>();

        taskMap.merge(originalMessageId, future, (oldValue, value) -> {
            if (oldValue instanceof CompletableFuture) {
                HashSet<CompletableFuture> taskSet = new HashSet<>();
                taskSet.add((CompletableFuture) oldValue);
                taskSet.add((CompletableFuture) value);
                return taskSet;
            } else if (oldValue instanceof Set) {
                ((Set<CompletableFuture>) oldValue).add((CompletableFuture) value);
                return oldValue;
            } else {
                throw new IllegalArgumentException("Unexpected task map value type: " + oldValue.getClass());
            }
        });

        return future;
    }
}
