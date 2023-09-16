package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApplianceEntity {

    @JsonProperty(required = true)
    private long applianceId;

    @JsonProperty(required = true)
    private String applianceUuid;

    @JsonProperty(required = true)
    private String applianceType;

    @JsonProperty(required = true)
    private String name;

    private String description;
}
