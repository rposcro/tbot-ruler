package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.BindingsAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyError;
import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class BindingEditSupport {

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
        try {
            bindingsAccessor.createBinding(dialog.getSenderUuid(), dialog.getReceiverUuid());
            notifyInfo("New binding created");
            return true;
        } catch(ClientCommunicationException e) {
            notifyError("Failed to request service create!");
        } catch(Exception e) {
            notifyError("Something's wrong!");
        }

        return false;
    }
}
