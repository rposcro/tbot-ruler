package com.tbot.ruler.controller.console.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class BindingReference {

    @NonNull
    private String uuid;

    private boolean actuatorUuid;
}
