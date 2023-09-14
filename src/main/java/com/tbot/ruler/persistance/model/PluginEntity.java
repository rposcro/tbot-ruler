package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PluginEntity {

    @NonNull
    @JsonProperty(required = true)
    private long pluginId;

    @NonNull
    @JsonProperty(required = true)
    private String pluginUuid;

    @NonNull
    @JsonProperty(required = true)
    private String builderClass;

    @NonNull
    @JsonProperty(required = true)
    private String name;

    private JsonNode configuration;

    private List<ThingEntity> things;
}
