package com.tbot.ruler.persistance.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookDTO {

    @NonNull
    @JsonProperty(required = true)
    private String uuid;

    @NonNull
    @JsonProperty(required = true)
    private String owner;

    @NonNull
    @JsonProperty(required = true)
    private String name;

    private String description;
}
