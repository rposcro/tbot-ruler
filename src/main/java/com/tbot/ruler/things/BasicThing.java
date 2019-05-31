package com.tbot.ruler.things;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class BasicThing implements Thing {

    private ThingId id;
    private ThingMetadata metadata;
    @Builder.Default
    private List<? extends Emitter> emitters = Collections.emptyList();
    @Builder.Default
    private List<? extends Collector> collectors = Collections.emptyList();
    @Builder.Default
    private List<? extends Actuator> actuators = Collections.emptyList();
}
