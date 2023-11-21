package com.tbot.ruler.console.views.validation;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class FormValidationResult {

    @Singular
    private final List<FieldValidator> failedValidators;

    @Singular
    private final List<FieldValidator> passedValidators;

    public boolean validationPassed() {
        return failedValidators.isEmpty();
    }
}
