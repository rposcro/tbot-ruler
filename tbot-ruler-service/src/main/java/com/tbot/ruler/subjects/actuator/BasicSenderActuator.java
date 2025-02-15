package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.subjects.AbstractSubject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Collection;
import java.util.function.Supplier;

@Getter
public class BasicSenderActuator extends AbstractSubject implements Actuator {

    private final MessagePublisher messagePublisher;
    private final Supplier<Message> messageSupplier;

    @Builder
    public BasicSenderActuator(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        @Singular Collection<JobBundle> jobBundles,
        @NonNull MessagePublisher messagePublisher,
        @NonNull Supplier<Message> messageSupplier
    ) {
        super(uuid, name, description, jobBundles);
        this.messagePublisher = messagePublisher;
        this.messageSupplier = messageSupplier;
    }

    public void sendMessage() {
        messagePublisher.publishMessage(messageSupplier.get());
    }
}
