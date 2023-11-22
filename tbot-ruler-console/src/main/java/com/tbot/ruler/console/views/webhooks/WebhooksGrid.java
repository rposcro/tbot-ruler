package com.tbot.ruler.console.views.webhooks;

import com.tbot.ruler.console.views.AbstractFilterableGridPanel;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.vaadin.flow.component.grid.GridVariant;

import java.util.Collections;
import java.util.function.Consumer;

public class WebhooksGrid extends AbstractFilterableGridPanel<WebhookResponse, WebhooksGridFilter> {

    public WebhooksGrid(Consumer<WebhookResponse> selectHandler) {
        super(new WebhooksGridFilter());

        setItems(Collections.emptyList());
        setUpColumns();
        setSizeFull();
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        asSingleSelect().addValueChangeListener(
                event -> selectHandler.accept(event.getValue()));
    }

    private void setUpColumns() {
        addFilteredColumn("Name", WebhookResponse::getName, gridFilter::setNameTerm);
        addFilteredColumn("Owner", WebhookResponse::getOwner, gridFilter::setOwnerTerm);
        addFilteredColumn("UUID", WebhookResponse::getWebhookUuid, gridFilter::setUuidTerm);
    }
}
