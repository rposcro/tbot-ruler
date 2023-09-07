package com.tbot.ruler.messages;

import com.tbot.ruler.exceptions.ServiceExecutionException;
import com.tbot.ruler.exceptions.ServiceTimeoutException;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Slf4j
@Service
    public class SynchronousMessagePublisher implements MessageDeliveryReportListener {

    private final static long DEFAULT_TIMEOUT = 60_000;

    private Map<Long, Object> futuresMap = new ConcurrentHashMap<>();

    private MessagePublisher messagePublisher;

    @Builder
    @Autowired
    public SynchronousMessagePublisher(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void deliveryReportCompleted(MessageDeliveryReport deliveryReport) {
        long originalMessageId = deliveryReport.getOriginalMessage().getId();
        Object mapped = futuresMap.remove(originalMessageId);

        if (mapped != null) {
            Stream<CompletableFuture<MessageDeliveryReport>> futureStream;

            if (mapped instanceof CompletableFuture) {
                futureStream = Stream.of((CompletableFuture<MessageDeliveryReport>) mapped);
            } else if (mapped instanceof Set) {
                futureStream = ((Set<CompletableFuture<MessageDeliveryReport>>) mapped).stream();
            } else {
                log.warn("Wrong type of futures map entry: {} for original message id", mapped.getClass(), originalMessageId);
                futureStream = Stream.empty();
            }

            futureStream.forEach(future -> deliverReport(future, deliveryReport));
        }
    }

    public MessageDeliveryReport publishAndWaitForReport(Message message) {
        return publishAndWaitForReport(message, DEFAULT_TIMEOUT);
    }

    public MessageDeliveryReport publishAndWaitForReport(Message message, long timeout) {
        CompletableFuture<MessageDeliveryReport> future = settleFuture(message.getId());

        try {
            messagePublisher.publishMessage(message);
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch(TimeoutException e) {
            removeFuture(message.getId(), future);
            throw new ServiceTimeoutException("Delivery report not received in scheduled time", e);
        } catch(Exception e) {
            removeFuture(message.getId(), future);
            throw new ServiceExecutionException("Delivery report could not be received", e);
        }
    }

    private void deliverReport(CompletableFuture<MessageDeliveryReport> future, MessageDeliveryReport deliveryReport) {
        future.complete(deliveryReport);
    }

    private void removeFuture(Long originalMessageId, CompletableFuture<MessageDeliveryReport> future) {
        futuresMap.computeIfPresent(originalMessageId, (key, mapping) -> {
            if (mapping instanceof Set) {
                Set<CompletableFuture<MessageDeliveryReport>> futureSet = (Set<CompletableFuture<MessageDeliveryReport>>) mapping;
                futureSet.remove(future);
                if (!futureSet.isEmpty()) {
                    return futureSet;
                }
            }
            return null;
        });
    }

    private CompletableFuture<MessageDeliveryReport> settleFuture(long originalMessageId) {
        CompletableFuture<MessageDeliveryReport> future = new CompletableFuture<>();

        futuresMap.merge(originalMessageId, future, (oldValue, value) -> {
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
