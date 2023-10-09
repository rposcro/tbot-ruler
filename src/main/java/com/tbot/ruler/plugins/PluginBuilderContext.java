package com.tbot.ruler.plugins;

import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.service.ServiceProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PluginBuilderContext {

    private ServiceProvider serviceProvider;
    private MessagePublisher messagePublisher;
    private SubjectStateService subjectStateService;
}
