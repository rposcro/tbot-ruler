package com.tbot.ruler.controller.admin.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class BindingResponse {

    @NonNull
    private String senderUuid;

    @NonNull
    private String receiverUuid;
}
