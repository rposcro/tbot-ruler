package com.tbot.ruler.console.views.validation;

import com.tbot.ruler.console.views.PopupNotifier;
import com.vaadin.flow.component.Component;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Supplier;

@Builder
@Getter
public class CustomFieldValidator implements FieldValidator<Component> {

    private Component component;
    private String message;
    private Supplier<Boolean> rule;

    public boolean checkRule() {
        return rule.get();
    }

    public void highlight(boolean on) {
        if (on) {
            PopupNotifier.notifyError(message);
        }
    }
}
