package com.tbot.ruler.plugins.ghost.singleactivator;

import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.things.AbstractActuator;
import com.tbot.ruler.things.thread.TaskTrigger;

import java.util.Optional;

public class SingleIntervalActuator extends AbstractActuator {

    public SingleIntervalActuator(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void acceptMessage(Message message) {
        super.acceptMessage(message);
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
        super.acceptDeliveryReport(deliveryReport);
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        return super.getTaskTrigger();
    }

}
