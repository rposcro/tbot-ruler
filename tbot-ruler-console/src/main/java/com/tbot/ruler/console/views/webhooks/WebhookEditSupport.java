package com.tbot.ruler.console.views.webhooks;

import com.tbot.ruler.console.clients.WebhooksClient;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

@SpringComponent
public class WebhookEditSupport {

    @Autowired
    private WebhooksClient webhooksClient;

    @Autowired
    private PopupNotifier popupNotifier;

    public List<WebhookResponse> fetchAllWebhooks() {
        return webhooksClient.getAllWebhooks();
    }

    public void launchWebhookEdit(WebhookResponse webhook, Consumer<WebhookEditDialog> submitHandler) {
        WebhookEditDialog.builder()
                .updateMode(true)
                .owners(webhooksClient.getOwners())
                .original(webhook)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchWebhookCreate(Consumer<WebhookEditDialog> submitHandler) {
        WebhookEditDialog.builder()
                .updateMode(false)
                .owners(webhooksClient.getOwners())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public boolean updateWebhook(WebhookEditDialog dialog) {
        try {
            WebhookUpdateRequest request = WebhookUpdateRequest.builder()
                    .name(dialog.getWebhookName())
                    .description(dialog.getWebhookDescription())
                    .build();
            webhooksClient.updateWebhook(dialog.getOriginal().getWebhookUuid(), request);
            popupNotifier.notifyInfo("Webhook updated");
            return true;
        } catch(ClientCommunicationException e) {
            popupNotifier.notifyError("Failed to request service update!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
        }

        return false;
    }

    public boolean createWebhook(WebhookEditDialog dialog) {
        try {
            WebhookCreateRequest request = WebhookCreateRequest.builder()
                    .name(dialog.getWebhookName())
                    .owner(dialog.getWebhookOwner())
                    .description(dialog.getWebhookDescription())
                    .build();
            webhooksClient.createWebhook(request);
            popupNotifier.notifyInfo("New webhook created");
            return true;
        } catch(ClientCommunicationException e) {
            popupNotifier.notifyError("Failed to request service create!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
        }

        return false;
    }
}
