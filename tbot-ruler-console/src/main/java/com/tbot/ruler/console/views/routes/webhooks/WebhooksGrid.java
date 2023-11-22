package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.views.components.EntityFilterableGrid;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;

import java.util.function.Consumer;

public class WebhooksGrid extends EntityFilterableGrid<WebhookResponse, WebhooksGridFilter> {

    public WebhooksGrid(Consumer<WebhookResponse> selectHandler) {
        super(new WebhooksGridFilter(), selectHandler);
        setUpColumns();
    }

    private void setUpColumns() {
        addFilterableColumn("Name", WebhookResponse::getName, gridFilter::setNameTerm);
        addFilterableColumn("Owner", WebhookResponse::getOwner, gridFilter::setOwnerTerm);
        addFilterableColumn("UUID", WebhookResponse::getWebhookUuid, gridFilter::setUuidTerm);
    }
}
