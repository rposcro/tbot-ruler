package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ActuatorEntity {

    @JsonProperty(required = true)
    private long actuatorId;

    @JsonProperty(required = true)
    private String actuatorUuid;

    @JsonProperty(required = true)
    private String thingUuid;

    @JsonProperty(required = true)
    private String reference;

    @JsonProperty(required = true)
    private String name;

    private String description;

    private JsonNode configuration;
}
