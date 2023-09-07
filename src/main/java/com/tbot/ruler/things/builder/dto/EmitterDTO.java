package com.tbot.ruler.things.builder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmitterDTO extends ConfigurableDTO implements ItemDTO {

    private String id;
    private String ref;
    private String name;
    private String description;
}
