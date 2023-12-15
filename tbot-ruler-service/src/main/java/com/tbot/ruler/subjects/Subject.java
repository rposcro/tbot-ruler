package com.tbot.ruler.subjects;

import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.task.SubjectTask;

import java.util.Collection;
import java.util.Collections;

public interface Subject {

    String getUuid();
    String getName();

    default String getDescription() {
        return "";
    }

    default Collection<SubjectTask> getAsynchronousSubjectTasks() { return Collections.emptyList(); }

    default Collection<JobBundle> getJobBundles() {
        return Collections.emptyList();
    }
}
