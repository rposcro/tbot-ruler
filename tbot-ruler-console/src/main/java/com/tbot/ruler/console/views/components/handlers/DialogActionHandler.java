package com.tbot.ruler.console.views.components.handlers;

import com.vaadin.flow.component.dialog.Dialog;

public interface DialogActionHandler<D extends Dialog> {

    void execute(D dialog);
}
