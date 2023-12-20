package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.views.components.EntityFilterableGrid;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;

public class WebhooksGrid extends EntityFilterableGrid<WebhookResponse> {

    public WebhooksGrid() {
        super(WebhookResponse.class, new String[] { "name", "owner", "webhookUuid"});
    }
}
