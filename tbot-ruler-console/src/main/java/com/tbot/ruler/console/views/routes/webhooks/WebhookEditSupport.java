package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.accessors.WebhooksAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.components.Prompt;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;
import static com.tbot.ruler.console.views.PopupNotifier.promptError;

@RouteScope
@SpringComponent
public class WebhookEditSupport {

    @Autowired
    private WebhooksAccessor webhooksAccessor;

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
                .title("Delete?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete")
                        .addLine("Webhook named " + webhook.getName())
                        .addLine("UUID: " + webhook.getWebhookUuid())
                        .addLine("?"))
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
            notifyInfo("Webhook updated");
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
        });
    }

    private boolean handlingExceptions(Runnable procedure) {
        try {
            procedure.run();
            return true;
        } catch(ClientCommunicationException e) {
            promptError(e.getMessage(), e.getResponseMessage());
        } catch(Exception e) {
            promptError("Something's wrong, check logs for details!", e.getMessage());
        }
        return false;
    }
}
