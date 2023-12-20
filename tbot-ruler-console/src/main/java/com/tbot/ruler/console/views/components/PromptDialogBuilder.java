package com.tbot.ruler.console.views.components;

import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.LinkedList;
import java.util.List;

public class PromptDialogBuilder<T> {

    private final PromptDialog<T> dialog;
    private final List<Button> actionButtons;

    public PromptDialogBuilder() {
        this.actionButtons = new LinkedList<>();
        this.dialog = new PromptDialog<T>();
    }

    public Dialog build() {
        if (actionButtons.size() >= 1) {
            actionButtons.get(0).getStyle().set("margin-left", "auto");
            actionButtons.get(actionButtons.size() - 1).getStyle().set("margin-right", "auto");
        }
        return dialog;
    }

    public PromptDialogBuilder title(String title) {
        dialog.setHeaderTitle(title);
        return this;
    }

    public PromptDialogBuilder prompt(Component component) {
        dialog.add(component);
        return this;
    }

    public PromptDialogBuilder prompt(String... promptLines) {
        dialog.add(new Prompt(promptLines));
        return this;
    }

    public PromptDialogBuilder promptedObject(T promptedObject) {
        dialog.setPromptedObject(promptedObject);
        return this;
    }

    public PromptDialogBuilder action(String actionTitle, PromptActionHandler actionHandler) {
        Button button = new Button(actionTitle, event -> actionHandler.actionTriggered(dialog));
        actionButtons.add(button);
        dialog.getFooter().add(button);
        return this;
    }
}
