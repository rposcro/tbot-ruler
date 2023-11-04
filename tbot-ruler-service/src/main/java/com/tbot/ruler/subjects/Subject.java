package com.tbot.ruler.subjects;

import com.tbot.ruler.task.Task;

import java.util.Collection;
import java.util.Collections;

public interface Subject {

    String getUuid();
    String getName();

    default String getDescription() {
        return "";
    }

    default Collection<Task> getAsynchronousTasks() { return Collections.emptyList(); }

}
