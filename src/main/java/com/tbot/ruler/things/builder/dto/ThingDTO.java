package com.tbot.ruler.things.builder.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThingDTO extends ConfigurableDTO implements ItemDTO {

    private String id;
    private String name;
    private String description;
    @JsonProperty("plugin")
    private String pluginAlias;
    private List<EmitterDTO> emitters = Collections.emptyList();
    private List<CollectorDTO> collectors = Collections.emptyList();
    private List<ActuatorDTO> actuators = Collections.emptyList();
}
