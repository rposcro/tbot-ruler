package com.tbot.ruler.console.views.validation;

import com.vaadin.flow.component.textfield.TextFieldBase;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Supplier;

@Builder
@Getter
public class TextFieldValidator implements FieldValidator<TextFieldBase<?, String>> {

    private TextFieldBase<?, String> component;
    private String message;
    private Supplier<Boolean> rule;

    public boolean checkRule() {
        return rule.get();
    }

    public void highlight(boolean on) {
        if (on) {
            component.getStyle().set("--vaadin-input-field-border-width", "1px");
            component.getStyle().set("--vaadin-input-field-border-color", HIGHLIGHT_COLOR);
            component.setHelperText(message);
        } else {
            component.getStyle().remove("--vaadin-input-field-border-width");
            component.getStyle().remove("--vaadin-input-field-border-color");
            component.setHelperText("");
        }
    }
}
