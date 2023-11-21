package com.tbot.ruler.controller.admin.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WebhookCreateRequest {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private String owner;

    @JsonProperty(required = true)
    private String description;
}
