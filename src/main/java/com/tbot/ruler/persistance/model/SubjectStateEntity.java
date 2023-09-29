package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SubjectStateEntity {

    @JsonProperty(required = true)
    private String subjectUuid;

    @JsonProperty(required = true)
    private JsonNode payload;
}
