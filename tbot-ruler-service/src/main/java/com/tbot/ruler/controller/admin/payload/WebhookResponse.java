package com.tbot.ruler.controller.admin.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class WebhookResponse {

    @NonNull
    private String webhookUuid;

    @NonNull
    private String owner;

    @NonNull
    private String name;

    private String description;
}
