package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.accessors.BindingsAccessor;
import com.tbot.ruler.console.accessors.BindingsModelAccessor;
import com.tbot.ruler.console.accessors.WebhooksAccessor;
import com.tbot.ruler.console.views.AbstractEditSupport;
import com.tbot.ruler.console.views.components.Prompt;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.tbot.ruler.console.views.routes.bindings.BindingsDialog;
import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class WebhookEditSupport extends AbstractEditSupport {

    @Autowired
    private WebhooksAccessor webhooksAccessor;

    @Autowired
    private BindingsModelAccessor bindingsModelAccessor;

    @Autowired
    private BindingsAccessor bindingsAccessor;

    public List<WebhookResponse> getAllWebhooks() {
        return webhooksAccessor.getAllWebhooks();
    }

    public void launchWebhookEdit(WebhookResponse webhook, EditDialogSubmittedHandler<WebhookEditDialog> submitHandler) {
        WebhookEditDialog.builder()
                .updateMode(true)
                .owners(webhooksAccessor.getAvailableOwners())
                .original(webhook)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchWebhookCreate(EditDialogSubmittedHandler<WebhookEditDialog> submitHandler) {
        WebhookEditDialog.builder()
                .updateMode(false)
                .owners(webhooksAccessor.getAvailableOwners())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchWebhookDelete(WebhookResponse webhook, PromptActionHandler deleteHandler) {
        PromptDialog.builder()
                .title("Delete Webhook?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete the webhook?")
                        .addLine("Name: " + webhook.getName())
                        .addLine("UUID: " + webhook.getWebhookUuid()))
                .build()
                .open();
    }

    public void launchAllBindingsDelete(WebhookResponse webhook, PromptActionHandler deleteHandler) {
        PromptDialog.builder()
                .title("Delete All Bindings?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete all bindings for the webhook?")
                        .addLine("Name: " + webhook.getName())
                        .addLine("UUID: " + webhook.getWebhookUuid()))
                .build()
                .open();
    }

    public void launchShowBindings(WebhookResponse webhook) {
        BindingsDialog.builder()
                .title("Bindings of webhook " + webhook.getName())
                .outboundBindings(bindingsModelAccessor.getSenderBindingsModels(webhook.getWebhookUuid()))
                .build()
                .open();
    }

    public boolean updateWebhook(WebhookEditDialog dialog) {
        return handlingExceptions(() -> {
            WebhookUpdateRequest request = WebhookUpdateRequest.builder()
                    .name(dialog.getWebhookName())
                    .description(dialog.getWebhookDescription())
                    .build();
            webhooksAccessor.updateWebhook(dialog.getOriginal().getWebhookUuid(), request);
            notifyInfo("Webhook %s updated", dialog.getOriginal().getWebhookUuid());
        });
    }

    public boolean createWebhook(WebhookEditDialog dialog) {
        return handlingExceptions(() -> {
            WebhookCreateRequest request = WebhookCreateRequest.builder()
                    .name(dialog.getWebhookName())
                    .owner(dialog.getWebhookOwner())
                    .description(dialog.getWebhookDescription())
                    .build();
            webhooksAccessor.createWebhook(request);
            notifyInfo("New webhook created");
        });
    }

    public boolean deleteWebhook(WebhookResponse webhook) {
        return handlingExceptions(() -> {
            webhooksAccessor.deleteWebhook(webhook.getWebhookUuid());
            notifyInfo("Webhook '%s' deleted", webhook.getName());
        });
    }

    public boolean deleteAllBindings(WebhookResponse webhook) {
        return handlingExceptions(() -> {
            bindingsAccessor.getSenderBindings(webhook.getWebhookUuid()).forEach(
                    binding -> bindingsAccessor.deleteBinding(webhook.getWebhookUuid(), binding.getReceiverUuid()));
            notifyInfo("All bindings for webhook '%s' deleted", webhook.getName());
        });
    }
}
