package com.tbot.ruler.subjects.actuator;

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
}
