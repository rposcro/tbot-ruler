package com.tbot.ruler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApplicationProblemDetails {

    private String message;
    private String stackTrace;
}
