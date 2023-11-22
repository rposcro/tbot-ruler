package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.views.components.AbstractFilterableGrid;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;

import java.util.function.Consumer;

public class WebhooksGrid extends AbstractFilterableGrid<WebhookResponse, WebhooksGridFilter> {

    public WebhooksGrid(Consumer<WebhookResponse> selectHandler) {
        super(new WebhooksGridFilter(), selectHandler);
        setUpColumns();
    }

    private void setUpColumns() {
        addFilteredColumn("Name", WebhookResponse::getName, gridFilter::setNameTerm);
        addFilteredColumn("Owner", WebhookResponse::getOwner, gridFilter::setOwnerTerm);
        addFilteredColumn("UUID", WebhookResponse::getWebhookUuid, gridFilter::setUuidTerm);
    }
}
