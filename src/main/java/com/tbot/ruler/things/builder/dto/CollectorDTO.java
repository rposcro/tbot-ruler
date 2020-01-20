package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.things.CollectorId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectorDTO extends ConfigurableDTO {

    private CollectorId id;
    private String ref;
    private String name;
    private String description;
}
