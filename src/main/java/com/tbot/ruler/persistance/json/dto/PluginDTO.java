package com.tbot.ruler.persistance.json.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThingPluginDTO extends ConfigurableDTO {

    private String alias;
    private String uuid;
    private String builderClass;
}
