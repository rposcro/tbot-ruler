package com.tbot.ruler.controller.admin.payload;

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
public class PluginCreateRequest {

    @JsonProperty(required = true)
    private String factoryClass;

    @JsonProperty(required = true)
    private String name;

    private JsonNode configuration;
}
