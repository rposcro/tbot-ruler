package com.tbot.ruler.plugins;

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
