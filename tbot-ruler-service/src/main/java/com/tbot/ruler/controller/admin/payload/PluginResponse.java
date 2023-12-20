package com.tbot.ruler.controller.admin.payload;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class PluginResponse {

    @NonNull
    private String pluginUuid;

    @NonNull
    private String factoryClass;

    @NonNull
    private String name;

    private JsonNode configuration;
}
