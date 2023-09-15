package com.tbot.ruler.things.builder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorDTO extends ConfigurableDTO implements ItemDTO {

    private String uuid;
    private String ref;
    private String name;
    private String description;
}
