package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.ActivationClaim;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.subjects.AbstractSubject;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public abstract class AbstractActuator extends AbstractSubject implements Actuator {

    private boolean active;

    protected AbstractActuator(String uuid, String name, String description) {
        super(uuid, name, description);
    }

    protected AbstractActuator(String uuid, String name, String description, JobBundle jobBundle) {
        super(uuid, name, description, Collections.singleton(jobBundle));
    }

    protected AbstractActuator(String uuid, String name, String description, Collection<JobBundle> jobBundles) {
        super(uuid, name, description, jobBundles);
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void acceptMessage(Message message) {
        if (message.isPayloadAs(ActivationClaim.class)) {
            ActivationClaim claim = message.getPayloadAs(ActivationClaim.class);
            active = claim.isToggle() ? !active : claim.isActivate();
            LoggerFactory.getLogger(this.getClass())
                .info("Changed actuator {} ({}) active state to {}",
                    getName(),
                    getUuid(),
                    active ? "active" : "inactive");
        } else {
            logIgnoredMessage(message);
        }
    }

    protected void consumeMessage(Message message, Class<?> payloadType, Consumer<Message> payloadConsumer) {
        if (message.isPayloadAs(payloadType)) {
            payloadConsumer.accept(message);
            return;
        } else if (message.isPayloadAs(ActivationClaim.class)) {
            ActivationClaim claim = message.getPayloadAs(ActivationClaim.class);
            active = claim.isToggle() ? !active : claim.isActivate();
            return;
        }

        logIgnoredMessage(message);
    }

    protected void consumeMessage(Message message, MessagePayloadConsumer... consumers) {
        for (MessagePayloadConsumer consumer : consumers) {
            if (consumePayload(message, consumer.payloadType, consumer.payloadConsumer)) {
                return;
            }
        }

        if (message.isPayloadAs(ActivationClaim.class)) {
            ActivationClaim claim = message.getPayloadAs(ActivationClaim.class);
            active = claim.isToggle() ? !active : claim.isActivate();
            return;
        }

        logIgnoredMessage(message);
    }

    private boolean consumePayload(Message message, Class<?> payloadType, Consumer<Message> payloadConsumer) {
        if (message.isPayloadAs(payloadType)) {
            payloadConsumer.accept(message);
            return true;
        }
        return false;
    }

    private void logIgnoredMessage(Message message) {
        LoggerFactory.getLogger(this.getClass())
            .info("Actuator {} ({}) ignored message {} from {}",
                getName(),
                getUuid(),
                message.getPayload().getClass().getSimpleName(),
                message.getSenderId());
    }

    @AllArgsConstructor
    protected class MessagePayloadConsumer {
        private final Class<?> payloadType;
        private final Consumer<Message> payloadConsumer;
    }
}
