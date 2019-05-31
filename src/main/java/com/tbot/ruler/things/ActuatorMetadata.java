package com.tbot.ruler.things;

import com.tbot.ruler.signals.SignalValueType;
import com.tbot.ruler.things.dto.ActuatorDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ActuatorMetadata {

    private ActuatorId id;
    private String name;
    private String description;
    private SignalValueType handledSignalType;
    
    public static ActuatorMetadata fromActuatorDTO(ActuatorDTO actuatorDTO) {
        return ActuatorMetadata.builder()
                .id(actuatorDTO.getId())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getName())
                .build();
    }
}
