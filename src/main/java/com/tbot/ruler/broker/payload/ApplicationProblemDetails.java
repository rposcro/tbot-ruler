package com.tbot.ruler.broker.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationProblemDetails {

    @NonNull
    private String message;

    @JsonInclude(NON_NULL)
    private Object payload;

    @JsonInclude(NON_NULL)
    private String stackTrace;
}
