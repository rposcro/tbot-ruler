package com.tbot.ruler.console.views.components;

import com.vaadin.flow.component.dialog.Dialog;

public class PromptDialog extends Dialog {

    public PromptDialog() {
        setModal(true);
        setDraggable(true);
        setResizable(false);
        setWidth("auto");
        setHeight("auto");
    }

    public static PromptDialogBuilder builder() {
        return new PromptDialogBuilder();
    }
}
