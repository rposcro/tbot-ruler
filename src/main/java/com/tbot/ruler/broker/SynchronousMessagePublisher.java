package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.ServiceExecutionException;
import com.tbot.ruler.exceptions.ServiceTimeoutException;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.Message;
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
public class SynchronousMessagePublisher implements MessagePublicationReportListener {

    private final static long DEFAULT_TIMEOUT = 60_000;

    private Map<Long, Object> futuresMap = new ConcurrentHashMap<>();

    private MessagePublisher messagePublisher;

    @Builder
    @Autowired
    public SynchronousMessagePublisher(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    public MessagePublicationReport publishAndWaitForReport(Message message) {
        return publishAndWaitForReport(message, DEFAULT_TIMEOUT);
    }

    public MessagePublicationReport publishAndWaitForReport(Message message, long timeout) {
        CompletableFuture<MessagePublicationReport> future = settleFuture(message.getId());

        try {
            messagePublisher.publishMessage(message);
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch(TimeoutException e) {
            removeFuture(message.getId(), future);
            throw new ServiceTimeoutException("Publication report not received in scheduled time", e);
        } catch(Exception e) {
            removeFuture(message.getId(), future);
            throw new ServiceExecutionException("Publication report could not be received", e);
        }
    }

    @Override
    public void publicationReportDelivered(MessagePublicationReport publicationReport) {
        handlePublicationReport(publicationReport);
    }

    @Override
    public void publicationReportSkipped(MessagePublicationReport publicationReport) {
        handlePublicationReport(publicationReport);
    }

    private void handlePublicationReport(MessagePublicationReport publicationReport) {
        long originalMessageId = publicationReport.getOriginalMessage().getId();
        Object mapped = futuresMap.remove(originalMessageId);

        if (mapped != null) {
            Stream<CompletableFuture<MessagePublicationReport>> futureStream;

            if (mapped instanceof CompletableFuture) {
                futureStream = Stream.of((CompletableFuture<MessagePublicationReport>) mapped);
            } else if (mapped instanceof Set) {
                futureStream = ((Set<CompletableFuture<MessagePublicationReport>>) mapped).stream();
            } else {
                log.warn("Wrong type of futures map entry: {} for original message id", mapped.getClass(), originalMessageId);
                futureStream = Stream.empty();
            }

            futureStream.forEach(future -> deliverReport(future, publicationReport));
        }
    }

    private void deliverReport(CompletableFuture<MessagePublicationReport> future, MessagePublicationReport publicationReport) {
        future.complete(publicationReport);
    }

    private void removeFuture(Long originalMessageId, CompletableFuture<MessagePublicationReport> future) {
        futuresMap.computeIfPresent(originalMessageId, (key, mapping) -> {
            if (mapping instanceof Set) {
                Set<CompletableFuture<MessagePublicationReport>> futureSet = (Set<CompletableFuture<MessagePublicationReport>>) mapping;
                futureSet.remove(future);
                if (!futureSet.isEmpty()) {
                    return futureSet;
                }
            }
            return null;
        });
    }

    private CompletableFuture<MessagePublicationReport> settleFuture(long originalMessageId) {
        CompletableFuture<MessagePublicationReport> future = new CompletableFuture<>();

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
