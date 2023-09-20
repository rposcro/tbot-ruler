package com.tbot.ruler.things;

import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

import java.util.Collection;
import java.util.List;

import static com.tbot.ruler.util.CollectionsUtil.orEmpty;

@Getter
public class BasicThing extends AbstractItem implements Thing {

    private final List<? extends Actuator> actuators;
    private final Collection<Task> asynchronousTasks;

    @Builder
    public BasicThing(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        @Singular List<? extends Actuator> actuators,
        @Singular Collection<Task> asynchronousTasks
    ) {
        super(uuid, name, description);
        this.actuators = orEmpty(actuators);
        this.asynchronousTasks = orEmpty(asynchronousTasks);
    }
}
