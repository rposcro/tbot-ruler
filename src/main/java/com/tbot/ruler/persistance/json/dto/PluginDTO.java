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
public class PluginDTO extends ConfigurableDTO {

    @NonNull
    @JsonProperty(required = true)
    private String uuid;

    @NonNull
    @JsonProperty(required = true)
    private String alias;

    @NonNull
    @JsonProperty(required = true)
    private String builderClass;
}
