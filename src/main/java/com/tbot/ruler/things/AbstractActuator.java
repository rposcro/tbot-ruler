package com.tbot.ruler.things;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.Optional;

@SuperBuilder
public abstract class AbstractActuator extends AbstractItem implements Actuator {

    @Builder.Default
    private Optional<Runnable> startUpTask = Optional.empty();
    @Builder.Default
    private Optional<Runnable> triggerableTask = Optional.empty();
    @Builder.Default
    private Optional<TaskTrigger> taskTrigger = Optional.empty();

    protected AbstractActuator(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public Optional<Runnable> getStartUpTask() {
        return startUpTask;
    }

    @Override
    public Optional<Runnable> getTriggerableTask() {
        return triggerableTask;
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        return taskTrigger;
    }

    @Override
    public void acceptMessage(Message message) {
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }
}
