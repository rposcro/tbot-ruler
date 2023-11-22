package com.tbot.ruler.console.views.bindings;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.PopupNotifier;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;

@SpringComponent
public class BindingEditSupport {

    @Autowired
    private BindingsDataSupport dataSupport;

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
            dataSupport.createBinding(dialog.getSenderUuid(), dialog.getReceiverUuid());
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
