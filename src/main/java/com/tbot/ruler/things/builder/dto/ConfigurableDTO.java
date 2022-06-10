package com.tbot.ruler.things.builder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.util.ParseUtil;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Getter
public abstract class ConfigurableDTO {

    private JsonNode configurationNode;
    private Map<String, String> configurationMap = Collections.emptyMap();;

    @JsonProperty("config")
    public void setConfigurationNode(JsonNode configurationNode) {
        this.configurationNode = configurationNode;
        this.configurationMap = new HashMap<>();
        Iterable<String> namesIterable = () -> configurationNode.fieldNames();
        StreamSupport.stream(namesIterable.spliterator(), false)
                .forEach(fieldName -> configurationMap.put(fieldName, configurationNode.get(fieldName).asText()));
    }

    public String getStringParameter(String paramName) {
        return configurationMap.get(paramName);
    }

    public String getStringParameter(String paramName, String defaultValue) {
        return configurationMap.getOrDefault(paramName, defaultValue);
    }

    public int getIntParameter(String paramName) {
        return ParseUtil.parseInt(configurationMap.get(paramName));
    }

    public int getIntParameter(String paramName, int defaultValue) {
        String value = configurationMap.get(paramName);
        return value != null ? ParseUtil.parseInt(value) : defaultValue;
    }
}
