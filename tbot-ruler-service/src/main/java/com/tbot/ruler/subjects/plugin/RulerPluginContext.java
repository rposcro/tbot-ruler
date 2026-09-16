package com.tbot.ruler.subjects.plugin;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.service.plugins.PluginConfigurationDeserializer;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class RulerPluginContext {

    @NonNull
    private String pluginUuid;

    @NonNull
    private String pluginName;

    private JsonNode pluginConfiguration;

    @NonNull
    private PluginConfigurationDeserializer pluginConfigurationDeserializer;
}
