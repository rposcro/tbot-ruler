package com.tbot.ruler.console.views.components;

import com.vaadin.flow.component.dialog.Dialog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptDialog<T> extends Dialog {

    private T promptedObject;

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
