package com.tbot.ruler.controller.subject.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorStateUpdateRequest {

    @JsonProperty(required = true)
    private String widgetUuid;

    @JsonProperty(defaultValue = "Object")
    private String payloadType;

    private JsonNode payload;
}
