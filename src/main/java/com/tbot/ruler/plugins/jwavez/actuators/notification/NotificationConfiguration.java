package com.tbot.ruler.plugins.jwavez.actuators.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationConfiguration {

    @JsonProperty(required = true)
    private int sourceNodeId;

    private int sourceEndPointId;

    @JsonProperty(defaultValue = "false")
    private boolean multiChannelOn;

    @JsonProperty(required = true)
    private int notificationType;

    @JsonProperty(required = true)
    private int[] onEvents;

    @JsonProperty(required = true)
    private int[] offEvents;
}
