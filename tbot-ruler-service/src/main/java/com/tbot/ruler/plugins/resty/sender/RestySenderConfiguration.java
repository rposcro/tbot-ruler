package com.tbot.ruler.plugins.resty.sender;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestySenderConfiguration {

    @JsonProperty(required = true)
    private String url;

    @JsonProperty(required = true)
    private String method;

    @JsonProperty(required = true)
    private String body;

    @JsonProperty(required = true)
    private Map<String, String> headers;
}
