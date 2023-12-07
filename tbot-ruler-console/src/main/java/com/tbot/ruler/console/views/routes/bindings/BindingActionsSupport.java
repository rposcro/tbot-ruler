package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.ActuatorsAccessor;
import com.tbot.ruler.console.accessors.BindingsAccessor;
import com.tbot.ruler.console.accessors.BindingsModelAccessor;
import com.tbot.ruler.console.accessors.WebhooksAccessor;
import com.tbot.ruler.console.accessors.model.BindingModel;
import com.tbot.ruler.console.views.AbstractActionsSupport;
import com.tbot.ruler.console.views.components.Prompt;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.console.views.components.handlers.DialogActionHandler;
import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class BindingActionsSupport extends AbstractActionsSupport {

    @Autowired
    private ActuatorsAccessor actuatorsAccessor;

    @Autowired
    private WebhooksAccessor webhooksAccessor;

    @Autowired
    private BindingsAccessor bindingsAccessor;

    @Autowired
    private BindingsModelAccessor bindingsModelAccessor;

    public void launchBindingCreate(
            DialogActionHandler<BindingsCreateDialog> bindHandler,
            DialogActionHandler<BindingsCreateDialog> finishHandler)
    {
        BindingsCreateDialog.builder()
                .webhooks(webhooksAccessor.getAllWebhooks())
                .actuators(actuatorsAccessor.getAllActuators())
                .existingBindings(bindingsAccessor.getAllBindings())
                .bindHandler(bindHandler)
                .finishHandler(finishHandler)
                .build()
                .open();
    }

    public void launchBindingDelete(BindingModel binding, PromptActionHandler deleteHandler) {
        PromptDialog.<BindingModel>builder()
                .title("Delete Binding?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .promptedObject(binding)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete this binding?")
                        .addLine("Sender Name: " + binding.getSenderName())
                        .addLine("Sender UUID: " + binding.getSenderUuid())
                        .addLine("Receiver Name: " + binding.getReceiverName())
                        .addLine("Receiver UUID: " + binding.getReceiverUuid()))
                .build()
                .open();
    }

    public void launchAllReceiversDialog(BindingModel binding) {
        BindingsDialog.builder()
                .title("Receivers of sender " + binding.getSenderName())
                .outboundBindings(bindingsModelAccessor.getSenderBindingsModels(binding.getSenderUuid()))
                .build()
                .open();
    }

    public void launchAllSendersDialog(BindingModel binding) {
        BindingsDialog.builder()
                .title("Senders of receiver " + binding.getReceiverName())
                .inboundBindings(bindingsModelAccessor.getReceiverBindingsModels(binding.getReceiverUuid()))
                .build()
                .open();
    }

    public boolean createBinding(String senderUuid, String receiverUuid) {
        return handlingExceptions(() -> {
            bindingsAccessor.createBinding(senderUuid, receiverUuid);
            notifyInfo("New binding created");
        });
    }

    public boolean deleteBinding(BindingModel binding) {
        return handlingExceptions(() -> {
            bindingsAccessor.deleteBinding(binding.getSenderUuid(), binding.getReceiverUuid());
            notifyInfo("Binding deleted");
        });
    }
}
