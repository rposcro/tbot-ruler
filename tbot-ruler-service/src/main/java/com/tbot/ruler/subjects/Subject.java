package com.tbot.ruler.subjects;

import com.tbot.ruler.jobs.JobBundle;

import java.util.Collection;
import java.util.Collections;

public interface Subject {

    String getUuid();
    String getName();

    default String getDescription() {
        return "";
    }

    default Collection<JobBundle> getJobBundles() {
        return Collections.emptyList();
    }

    default boolean hasJobs() {
        return getJobBundles() != null && !getJobBundles().isEmpty();
    }
}
