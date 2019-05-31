package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.EmitterMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CronSchedulerEmitter implements Emitter {

    private EmitterId id;
    private EmitterMetadata metadata;
}
