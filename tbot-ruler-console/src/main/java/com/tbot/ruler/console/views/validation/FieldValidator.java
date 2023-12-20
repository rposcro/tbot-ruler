package com.tbot.ruler.console.views.validation;

import com.vaadin.flow.component.Component;

public interface FieldValidator<T extends Component> {

    static final String HIGHLIGHT_COLOR = "#ff8080";

    T getComponent();
    String getMessage();
    boolean checkRule();
    void highlight(boolean on);
}
