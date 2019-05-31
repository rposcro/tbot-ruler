package com.tbot.ruler.things.dto;

import com.tbot.ruler.things.ActuatorId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorDTO extends ConfigurableDTO {

    private String ref;
    private String name;
    private ActuatorId id;
}
