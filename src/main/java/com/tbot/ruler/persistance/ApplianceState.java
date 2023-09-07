package com.tbot.ruler.persistance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NonNull
@AllArgsConstructor
public class ApplianceState {

    @NotNull
    private String key;
    @NotNull
    private Class<?> valueClass;
    @NotNull
    private Object value;
}
