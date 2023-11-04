package com.tbot.ruler.plugins.email;

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
public class EmailSenderConfiguration {

    @JsonProperty(required = true)
    private String mailSenderHost;
    @JsonProperty(required = true)
    private int mailSenderPort;
    @JsonProperty(required = true)
    private String mailSenderUserName;
    @JsonProperty(required = true)
    private String mailSenderPassword;
}
