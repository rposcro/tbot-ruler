package com.tbot.ruler.things.dto;

import com.tbot.ruler.things.EmitterId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmitterDTO extends ConfigurableDTO {

    private EmitterId id;
    private String ref;
    private String name;
}
