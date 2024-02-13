package com.tbot.ruler.console.views.components.handlers;

import com.vaadin.flow.component.dialog.Dialog;

public interface EditDialogSubmittedHandler<ED extends Dialog> {

    void dialogSubmitted(ED entity);
}
