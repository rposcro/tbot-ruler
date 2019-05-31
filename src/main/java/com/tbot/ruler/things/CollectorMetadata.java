package com.tbot.ruler.things;

import com.tbot.ruler.things.dto.CollectorDTO;
import com.tbot.ruler.signals.SignalValueType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class CollectorMetadata {

    private CollectorId id;
    private String name;
    private String description;
    private SignalValueType collectedSignalType;
    
    public static CollectorMetadata fromCollectorDTO(CollectorDTO collectorDTO) {
        return CollectorMetadata.builder()
                .id(collectorDTO.getId())
                .name(collectorDTO.getName())
                .description(collectorDTO.getName())
                .build();
    }
}
