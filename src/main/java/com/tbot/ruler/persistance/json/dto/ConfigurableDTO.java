package com.tbot.ruler.persistance.json.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.util.ParseUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class ConfigurableDTO {

    private JsonNode configuration;

    @JsonIgnore
    private Map<String, String> configurationMap = Collections.emptyMap();;

    @JsonProperty
    public void setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
        this.configurationMap = new HashMap<>();
        Iterable<String> namesIterable = () -> configuration.fieldNames();
        StreamSupport.stream(namesIterable.spliterator(), false)
                .forEach(fieldName -> configurationMap.put(fieldName, configuration.get(fieldName).asText()));
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

    public long getLongParameter(String paramName) {
        return ParseUtil.parseLong(configurationMap.get(paramName));
    }

    public long getLongParameter(String paramName, long defaultValue) {
        String value = configurationMap.get(paramName);
        return value != null ? ParseUtil.parseLong(value) : defaultValue;
    }
}
