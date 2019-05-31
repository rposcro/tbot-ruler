package com.tbot.ruler.things.dto;

import com.tbot.ruler.appliances.ApplianceId;
import lombok.Data;

@Data
public class ApplianceDTO {
    
    private ApplianceId id;
    private String type;
    private String name;
    private String description;
}