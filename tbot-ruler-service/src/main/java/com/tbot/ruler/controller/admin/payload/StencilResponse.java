package com.tbot.ruler.controller.admin.payload;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class StencilResponse {

    @NonNull
    private String stencilUuid;

    @NonNull
    private String owner;

    @NonNull
    private String type;

    @NonNull
    private JsonNode payload;
}
