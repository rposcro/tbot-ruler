package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.RouteBindingsAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.PopupNotifier;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@RouteScope
@SpringComponent
public class BindingEditSupport {

    @Autowired
    private RouteBindingsAccessor bindingsAccessor;

    @Autowired
    private PopupNotifier popupNotifier;

    public void launchBindingCreate(Consumer<BindingEditDialog> submitHandler) {
        BindingEditDialog.builder()
                .updateMode(false)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public boolean createBinding(BindingEditDialog dialog) {
        try {
            bindingsAccessor.createBinding(dialog.getSenderUuid(), dialog.getReceiverUuid());
            popupNotifier.notifyInfo("New binding created");
            return true;
        } catch(ClientCommunicationException e) {
            popupNotifier.notifyError("Failed to request service create!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
        }

        return false;
    }
}
