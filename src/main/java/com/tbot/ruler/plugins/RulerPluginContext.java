package com.tbot.ruler.plugins;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.service.things.SubjectStateService;
import com.tbot.ruler.subjects.service.ServiceProvider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RulerPluginContext {

    private String pluginUuid;
    private String pluginName;
    private JsonNode pluginConfiguration;

    private ServiceProvider serviceProvider;
    private MessagePublisher messagePublisher;
    private SubjectStateService subjectStateService;
}
