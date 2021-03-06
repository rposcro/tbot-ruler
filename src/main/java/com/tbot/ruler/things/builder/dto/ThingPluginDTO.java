package com.tbot.ruler.things.builder.dto;

import java.util.List;

import lombok.Data;

@Data
public class ThingPluginDTO {
    private String alias;
    private String builder;
    private List<String> emitters;
    private List<String> collectors;
    private List<String> actuators;
}
