package com.tbot.ruler.subjects.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RulerPluginContext {

    private String pluginUuid;
    private String pluginName;
    private JsonNode pluginConfiguration;
}
