package com.tbot.ruler.subjects.actuator;

import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.subjects.AbstractSubject;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractActuator extends AbstractSubject implements Actuator {

    protected AbstractActuator(String uuid, String name, String description) {
        this(uuid, name, description, (Collection<JobBundle>) null);
    }

    protected AbstractActuator(String uuid, String name, String description, Collection<JobBundle> jobBundles) {
        super(uuid, name, description, null, jobBundles);
    }

    protected AbstractActuator(String uuid, String name, String description, JobBundle jobBundle) {
        super(uuid, name, description, null, Collections.singleton(jobBundle));
    }
}
