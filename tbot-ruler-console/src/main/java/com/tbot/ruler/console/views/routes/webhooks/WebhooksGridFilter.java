package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.views.components.GridFilter;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import lombok.Setter;

@Setter
public class WebhooksGridFilter implements GridFilter<WebhookResponse> {

    private String nameTerm;
    private String ownerTerm;
    private String uuidTerm;
    private String descriptionTerm;

    @Override
    public boolean test(WebhookResponse webhook) {
        return matches(webhook.getName(), nameTerm)
                && matches(webhook.getOwner(), ownerTerm)
                && matches(webhook.getWebhookUuid(), uuidTerm)
                && matches(webhook.getDescription(), descriptionTerm)
                ;
    }
}
