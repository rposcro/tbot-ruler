package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.accessors.WebhooksAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

@RouteScope
@SpringComponent
public class WebhookEditSupport {

    @Autowired
    private WebhooksAccessor webhooksAccessor;

    @Autowired
    private PopupNotifier popupNotifier;

    public List<WebhookResponse> getAllWebhooks() {
        return webhooksAccessor.getAllWebhooks();
    }

    public void launchWebhookEdit(WebhookResponse webhook, Consumer<WebhookEditDialog> submitHandler) {
        WebhookEditDialog.builder()
                .updateMode(true)
                .owners(webhooksAccessor.getAvailableOwners())
                .original(webhook)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchWebhookCreate(Consumer<WebhookEditDialog> submitHandler) {
        WebhookEditDialog.builder()
                .updateMode(false)
                .owners(webhooksAccessor.getAvailableOwners())
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
            webhooksAccessor.updateWebhook(dialog.getOriginal().getWebhookUuid(), request);
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
            webhooksAccessor.createWebhook(request);
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
