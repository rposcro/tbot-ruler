package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class HealthCheckEmitter implements Emitter {

    private EmitterId id;
    private EmitterMetadata metadata;
}
