package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.things.ItemId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectorDTO extends ConfigurableDTO {

    private ItemId id;
    private String ref;
    private String name;
    private String description;
}
