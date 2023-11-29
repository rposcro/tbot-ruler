package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.BindingsAccessor;
import com.tbot.ruler.console.views.AbstractEditSupport;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class BindingEditSupport extends AbstractEditSupport {

    @Autowired
    private BindingsAccessor bindingsAccessor;

    public void launchBindingCreate(EditDialogSubmittedHandler<BindingEditDialog> submitHandler) {
        BindingEditDialog.builder()
                .updateMode(false)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public boolean createBinding(BindingEditDialog dialog) {
        return handlingExceptions(() -> {
            bindingsAccessor.createBinding(dialog.getSenderUuid(), dialog.getReceiverUuid());
            notifyInfo("New binding created");
        });
    }
}
