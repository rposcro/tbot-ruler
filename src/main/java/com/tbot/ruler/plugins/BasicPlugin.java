package com.tbot.ruler.plugins;

import com.tbot.ruler.things.Thing;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.List;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public class BasicPlugin implements Plugin {

    private final String uuid;
    private final String name;
    private final List<? extends Thing> things;
    private final Collection<Task> asynchronousTasks;

    @Builder
    public BasicPlugin(
            String uuid,
            String name,
            List<? extends Thing> things,
            @Singular Collection<Task> asynchronousTasks) {
        this.uuid = uuid;
        this.name = name;
        this.things = orEmpty(things);
        this.asynchronousTasks = orEmpty(asynchronousTasks);
    }
}
