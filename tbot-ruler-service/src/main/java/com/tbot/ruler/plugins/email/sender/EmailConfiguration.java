package com.tbot.ruler.plugins.email.sender;

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
public class EmailConfiguration {

    @JsonProperty(required = true)
    private String emailFrom;
    @JsonProperty(required = true)
    private String emailTo;
    @JsonProperty(required = true)
    private String emailTitle;
    @JsonProperty(required = true)
    private String emailBodyTemplate;
}
