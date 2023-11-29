package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.console.views.components.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "webhooks", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Webhooks Dashboard")
public class WebhooksDashboard extends VerticalLayout {

    private final WebhookEditSupport editSupport;

    private final EntityPropertiesPanel<WebhookResponse> webhookPanel;
    private final WebhooksGrid webhooksGrid;

    @Autowired
    public WebhooksDashboard(WebhookEditSupport webhookEditSupport) {
        this.editSupport = webhookEditSupport;
        this.webhooksGrid = constructGrid();
        this.webhookPanel = constructWebhookPanel();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Webhook");
        createButton.addClickListener(event -> editSupport.launchWebhookCreate(this::handleCreateWebhook));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            webhooksGrid.setItems(editSupport.getAllWebhooks());
            webhookPanel.getStyle().set("margin-top", "0px");
            content.add(webhooksGrid, webhookPanel);
            content.setFlexGrow(3, webhooksGrid);
            content.setFlexGrow(1, webhookPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading webhooks ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private WebhooksGrid constructGrid() {
        WebhooksGrid grid = new WebhooksGrid();
        grid.setSelectHandler(webhook -> webhookPanel.applyToEntity(webhook));
        return grid;
    }

    private EntityPropertiesPanel<WebhookResponse> constructWebhookPanel() {
        return EntityPropertiesPanel.<WebhookResponse>builder()
                .beanType(WebhookResponse.class)
                .properties(new String[] { "name", "owner", "webhookUuid", "description" } )
                .editHandler(() -> editSupport.launchWebhookEdit(gridSelection(), this::handleUpdateWebhook))
                .deleteHandler(() -> editSupport.launchWebhookDelete(gridSelection(), this::handleDeleteWebhook))
                .build();
    }

    private WebhookResponse gridSelection() {
        return webhooksGrid.asSingleSelect().getValue();
    }

    private void handleUpdateWebhook(WebhookEditDialog dialog) {
        if (editSupport.updateWebhook(dialog)) {
            webhooksGrid.setItems(editSupport.getAllWebhooks());
            dialog.close();
        }
    }

    private void handleCreateWebhook(WebhookEditDialog dialog) {
        if (editSupport.createWebhook(dialog)) {
            webhooksGrid.setItems(editSupport.getAllWebhooks());
            dialog.close();
        }
    }

    private void handleDeleteWebhook(PromptDialog dialog) {
        if (editSupport.deleteWebhook(webhookPanel.getCurrentEntity())) {
            webhooksGrid.setItems(editSupport.getAllWebhooks());
            dialog.close();
        }
    }
}
