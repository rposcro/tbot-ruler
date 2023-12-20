package com.tbot.ruler.subjects;

import com.tbot.ruler.jobs.JobBundle;
import lombok.Getter;

import java.util.Collection;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public abstract class AbstractSubject implements Subject {

    protected String uuid;
    protected String name;
    protected String description;
    protected Collection<JobBundle> jobBundles;

    protected AbstractSubject(String uuid, String name) {
        this(uuid, name, null);
    }

    protected AbstractSubject(String uuid, String name, String description) {
        this(uuid, name, description, null);
    }

    protected AbstractSubject(
            String uuid,
            String name,
            String description,
            Collection<JobBundle> jobBundles) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.jobBundles = orEmpty(jobBundles);
    }
}
