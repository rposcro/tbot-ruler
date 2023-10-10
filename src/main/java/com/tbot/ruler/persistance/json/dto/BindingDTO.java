package com.tbot.ruler.persistance.json.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindingDTO {

    @NonNull
    @JsonProperty(required = true)
    private String senderUuid;

    @NonNull
    @JsonProperty(required = true)
    private List<String> receiversUuid;
}
