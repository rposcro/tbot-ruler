package com.tbot.ruler.console.views.validation;

import com.tbot.ruler.console.exceptions.ViewRenderException;
import com.tbot.ruler.console.utils.FormUtils;
import com.tbot.ruler.console.views.components.JsonEditor;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextFieldBase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class FormValidator {

    private List<FieldValidator> fieldValidators = new LinkedList<>();

    public FormValidationResult validate() {
        List<FieldValidator> failedRules = new ArrayList<>(fieldValidators.size());
        List<FieldValidator> passedRules = new ArrayList<>(fieldValidators.size());

        for (FieldValidator validator: fieldValidators) {
            if (validator.checkRule()) {
                passedRules.add(validator);
                validator.highlight(false);
            } else {
                failedRules.add(validator);
                validator.highlight(true);
            }
        }

        return FormValidationResult.builder()
                .failedValidators(failedRules)
                .passedValidators(passedRules)
                .build();
    }

    public FormValidator notEmpty(TextFieldBase<? extends TextFieldBase, String> formField) {
        fieldValidators.add(TextFieldValidator.builder()
                        .component(formField)
                        .message(formField.getLabel() + " cannot be empty")
                        .rule(() -> notEmpty(formField.getValue()))
                        .build());
        return this;
    }

    public FormValidator notEmpty(Select<?> formField) {
        fieldValidators.add(SelectValidator.builder()
                        .component(formField)
                        .message(formField.getLabel() + " must be selected")
                        .rule(() -> formField.getValue() != null)
                        .build());
        return this;
    }

    public FormValidator validJson(TextArea formField) {
        fieldValidators.add(TextFieldValidator.builder()
                .component(formField)
                .message(formField.getLabel() + " must be a valid json")
                .rule(() -> {
                    try {
                        FormUtils.asJsonNode(formField.getValue());
                        return true;
                    } catch(ViewRenderException e) {
                        return false;
                    }
                })
                .build());
        return this;
    }

    public FormValidator validJson(JsonEditor formField) {
        fieldValidators.add(CustomFieldValidator.builder()
                .component(formField)
                .message(formField.getLabel() + " must be a valid json")
                .rule(() -> {
                    try {
                        FormUtils.asJsonNode(formField.getJsonString());
                        return true;
                    } catch(ViewRenderException e) {
                        return false;
                    }
                })
                .build());
        return this;
    }

    private boolean notEmpty(String text) {
        return text != null && text.trim().length() > 0;
    }
}
