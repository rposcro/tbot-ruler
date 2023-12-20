package com.tbot.ruler.controller.admin.payload;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ThingResponse {

    @NonNull
    private String thingUuid;

    @NonNull
    private String name;

    private String description;

    private JsonNode configuration;
}
