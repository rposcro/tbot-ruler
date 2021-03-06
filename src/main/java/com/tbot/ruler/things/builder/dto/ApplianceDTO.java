package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.things.ApplianceId;
import lombok.Data;

@Data
public class ApplianceDTO {
    
    private ApplianceId id;
    private String type;
    private String name;
    private String description;
}