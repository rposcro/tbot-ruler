package com.tbot.ruler.things.builder.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThingPluginDTO extends ConfigurableDTO {

    private String alias;
    private String uuid;
    private String builder;
    private String builderClass;

    private List<String> emitters;
    private List<String> collectors;
    private List<String> actuators;
}
