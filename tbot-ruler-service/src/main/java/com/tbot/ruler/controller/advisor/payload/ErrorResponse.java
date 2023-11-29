package com.tbot.ruler.controller.advisor.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @NonNull
    private String message;

    @JsonInclude(NON_NULL)
    private Object payload;

    @JsonInclude(NON_NULL)
    private String stackTrace;
}
