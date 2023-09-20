package com.tbot.ruler.things;

import com.tbot.ruler.threads.Task;

import java.util.Collection;
import java.util.Collections;

public interface TaskBasedItem extends Item {

    default Collection<Task> getAsynchronousTasks() { return Collections.emptyList(); }
}
