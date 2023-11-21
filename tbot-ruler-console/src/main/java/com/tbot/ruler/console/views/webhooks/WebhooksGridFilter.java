package com.tbot.ruler.console.views.webhooks;

import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import lombok.Setter;

@Setter
public class WebhooksGridFilter {

    private String nameTerm;
    private String ownerTerm;
    private String uuidTerm;
    private String descriptionTerm;

    public boolean test(WebhookResponse webhook) {
        return matches(webhook.getName(), nameTerm)
                && matches(webhook.getOwner(), ownerTerm)
                && matches(webhook.getWebhookUuid(), uuidTerm)
                && matches(webhook.getDescription(), descriptionTerm)
                ;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
