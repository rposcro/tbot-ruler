package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.task.Task;

import java.util.Collection;

public abstract class AbstractActuator extends AbstractSubject implements Actuator {

    protected AbstractActuator(String uuid, String name, String description) {
        this(uuid, name, description, null);
    }

    protected AbstractActuator(String uuid, String name, String description, Collection<Task> asynchronousTasks) {
        super(uuid, name, description, asynchronousTasks);
    }

    @Override
    public void acceptMessage(Message message) {
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }
}
