package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.things.ActuatorId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorDTO extends ConfigurableDTO {

    private ActuatorId id;
    private String ref;
    private String name;
    private String description;
}
