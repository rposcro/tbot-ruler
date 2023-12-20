package com.tbot.ruler.console.accessors.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BindingModel {

    public final static String SENDER_TYPE_ACTUATOR = "Actuator";
    public final static String SENDER_TYPE_WEBHOOK = "Webhook";

    private String senderUuid;
    private String senderType;
    private String senderName;

    private String receiverUuid;
    private String receiverName;

    public boolean isSenderActuator() {
        return SENDER_TYPE_ACTUATOR.equals(senderType);
    }

    public boolean isSenderWebhook() {
        return SENDER_TYPE_WEBHOOK.equals(senderType);
    }
}
