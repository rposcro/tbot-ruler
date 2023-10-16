package com.tbot.ruler.persistance.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ActuatorDTO extends ConfigurableDTO {

    @NonNull
    @JsonProperty(required = true)
    private String uuid;

    @NonNull
    @JsonProperty(required = true)
    private String pluginUuid;

    @NonNull
    @JsonProperty(required = true)
    private String thingUuid;

    @NonNull
    @JsonProperty(required = true)
    private String ref;

    @NonNull
    @JsonProperty(required = true)
    private String name;

    private String description;
}
