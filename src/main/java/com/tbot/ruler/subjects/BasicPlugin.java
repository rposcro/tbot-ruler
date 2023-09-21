package com.tbot.ruler.subjects;

import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;
import java.util.List;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public class BasicPlugin extends AbstractSubject implements Plugin {

    private final List<? extends Thing> things;

    @Builder
    public BasicPlugin(
            String uuid,
            String name,
            List<? extends Thing> things,
            @Singular Collection<Task> asynchronousTasks) {
        super(uuid, name, "", asynchronousTasks);
        this.things = orEmpty(things);
    }
}
