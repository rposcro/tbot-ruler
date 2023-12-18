package com.tbot.ruler.controller.admin.payload;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ActuatorResponse {

    @NonNull
    private String actuatorUuid;

    @NonNull
    private String thingUuid;

    @NonNull
    private String pluginUuid;

    @NonNull
    private String reference;

    @NonNull
    private String name;

    private String description;

    private JsonNode configuration;

    private boolean relaunchRequired;
}
