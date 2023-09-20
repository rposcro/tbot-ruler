package com.tbot.ruler.things;

import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.task.Task;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractActuator extends AbstractItem implements Actuator {

    @Getter
    private final Collection<Task> asynchronousTasks;

    protected AbstractActuator(String uuid, String name, String description) {
        super(uuid, name, description);
        this.asynchronousTasks = Collections.emptyList();
    }

    protected AbstractActuator(String uuid, String name, String description, Collection<Task> asynchronousTasks) {
        super(uuid, name, description);
        this.asynchronousTasks = asynchronousTasks;
    }

    @Override
    public void acceptMessage(Message message) {
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }
}
