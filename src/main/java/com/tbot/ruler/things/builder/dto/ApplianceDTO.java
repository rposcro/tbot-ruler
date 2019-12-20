package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.things.ItemId;
import lombok.Data;

@Data
public class ApplianceDTO {
    
    private ItemId id;
    private String type;
    private String name;
    private String description;
}