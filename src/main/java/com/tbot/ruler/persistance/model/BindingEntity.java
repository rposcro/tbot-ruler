package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BindingEntity {

    @JsonProperty(required = true)
    private String senderUuid;

    @JsonProperty(required = true)
    private String receiverUuid;
}
