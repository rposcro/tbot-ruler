package com.tbot.ruler.console.views.components.handlers;

import com.tbot.ruler.console.views.components.AbstractEditDialog;

public interface EditDialogSubmittedHandler<ED extends AbstractEditDialog> {

    void dialogSubmitted(ED entity);
}
