package com.tbot.ruler.console.views.bindings;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BindingModel {

    private String senderUuid;
    private String senderType;
    private String senderName;

    private String receiverUuid;
    private String receiverName;
}
