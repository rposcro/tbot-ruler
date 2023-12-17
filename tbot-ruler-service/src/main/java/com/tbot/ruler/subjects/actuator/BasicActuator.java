package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.subjects.AbstractSubject;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Collection;
import java.util.Optional;

@Getter
public class BasicActuator extends AbstractSubject implements Actuator {

    private final Optional<MessageReceiver> messageReceiver;

    @Builder
    public BasicActuator(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        @Singular Collection<JobBundle> jobBundles,
        MessageReceiver messageReceiver
    ) {
        super(uuid, name, description, jobBundles);
        this.messageReceiver = Optional.ofNullable(messageReceiver);
    }

    @Override
    public void acceptMessage(Message message) {
        messageReceiver.ifPresent(receiver -> receiver.acceptMessage(message));
    }
}
