package com.tbot.ruler.things;

import com.tbot.ruler.things.dto.ThingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ThingMetadata {

    private ThingId id;
    private String name;
    private String description;
    
    public static ThingMetadata fromThingMetadata(ThingDTO thingDTO) {
        return ThingMetadata.builder()
                .id(thingDTO.getId())
                .name(thingDTO.getName())
                .description(thingDTO.getName())
                .build();
    }
}
