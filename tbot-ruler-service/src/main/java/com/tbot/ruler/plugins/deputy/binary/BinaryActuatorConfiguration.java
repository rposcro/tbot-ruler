package com.tbot.ruler.plugins.deputy.binary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BinaryActuatorConfiguration {

    @JsonProperty(required = true)
    private Integer pinNumber;
}
