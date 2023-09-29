package com.tbot.ruler.subjects;

import com.tbot.ruler.task.Task;
import lombok.Getter;

import java.util.Collection;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public abstract class AbstractSubject implements Subject {

    private final String uuid;
    private final String name;
    private final String description;
    private final Collection<Task> asynchronousTasks;

    protected AbstractSubject(String uuid, String name, String description) {
        this(uuid, name, description, null);
    }

    protected AbstractSubject(String uuid, String name, String description, Collection<Task> asynchronousTasks) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.asynchronousTasks = orEmpty(asynchronousTasks);
    }
}
