package com.tbot.ruler.subjects;

import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.task.SubjectTask;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public abstract class AbstractSubject implements Subject {

    protected String uuid;
    protected String name;
    protected String description;
    protected Collection<SubjectTask> asynchronousSubjectTasks;
    protected Collection<JobBundle> jobBundles;

    protected AbstractSubject(String uuid, String name) {
        this(uuid, name, null, (Collection<SubjectTask>) null);
    }

    protected AbstractSubject(String uuid, String name, String description) {
        this(uuid, name, description, (Collection<SubjectTask>) null);
    }

    protected AbstractSubject(String uuid, String name, String description, Collection<SubjectTask> asynchronousSubjectTasks) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.asynchronousSubjectTasks = orEmpty(asynchronousSubjectTasks);
        this.jobBundles = Collections.emptySet();
    }

    protected AbstractSubject(
            String uuid,
            String name,
            String description,
            Collection<SubjectTask> asynchronousSubjectTasks,
            Collection<JobBundle> jobBundles) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.asynchronousSubjectTasks = orEmpty(asynchronousSubjectTasks);
        this.jobBundles = orEmpty(jobBundles);
    }
}
