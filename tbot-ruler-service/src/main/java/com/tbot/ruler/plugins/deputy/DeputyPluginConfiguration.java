package com.tbot.ruler.plugins.deputy;

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
public class DeputyPluginConfiguration {

    @JsonProperty(required = true)
    private String host;

    @JsonProperty(required = true)
    private String port;

    @JsonProperty(required = true)
    private String path;
}
