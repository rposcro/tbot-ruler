package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ThingEntity {

    @JsonProperty(required = true)
    private long thingId;

    @JsonProperty(required = true)
    private String thingUuid;

    @JsonProperty(required = true)
    private String pluginUuid;

    @JsonProperty(required = true)
    private String name;

    private String description;

    private JsonNode configuration;

    private List<ActuatorEntity> actuators;
}
