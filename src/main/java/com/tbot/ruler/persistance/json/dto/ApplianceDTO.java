package com.tbot.ruler.persistance.json.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplianceDTO implements ItemDTO {
    
    private String uuid;
    private String type;
    private String name;
    private String description;
}