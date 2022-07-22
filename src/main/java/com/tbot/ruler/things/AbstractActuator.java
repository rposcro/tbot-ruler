package com.tbot.ruler.things;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.thread.TaskTrigger;

import java.util.Optional;

public abstract class AbstractActuator extends AbstractItem<ActuatorId> implements Actuator {

    protected AbstractActuator(ActuatorId id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public Optional<Runnable> getTriggerableTask() {
        return Optional.empty();
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        return Optional.empty();
    }

    @Override
    public Optional<Runnable> getStartUpTask() {
        return Optional.empty();
    }

    @Override
    public void acceptMessage(Message message) {
    }

    @Override
    public void acceptDeliveryReport(DeliveryReport deliveryReport) {
    }
}
