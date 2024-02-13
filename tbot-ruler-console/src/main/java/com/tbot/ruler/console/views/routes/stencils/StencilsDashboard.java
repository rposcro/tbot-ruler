package com.tbot.ruler.console.views.routes.stencils;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "stencils", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Stencils Dashboard")
public class StencilsDashboard extends VerticalLayout {

    private final StencilsActionsSupport actionsSupport;

    private final StencilsGrid stencilsGrid;

    @Autowired
    public StencilsDashboard(StencilsActionsSupport actionsSupport) {
        this.actionsSupport = actionsSupport;
        this.stencilsGrid = constructGrid();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Stencil");
        createButton.setEnabled(true);
        createButton.addClickListener(event -> actionsSupport.launchStencilCreate(this::handleStencilCreate));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            content.add(stencilsGrid);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading stencils ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private StencilsGrid constructGrid() {
        StencilsGrid grid = new StencilsGrid();
        grid.addContextMenuAction("Edit", stencilResponse -> actionsSupport.launchStencilEdit(stencilResponse, this::handleStencilUpdate));
        grid.addContextMenuAction("Edit Payload", stencilResponse -> actionsSupport.launchStencilPayloadEdit(stencilResponse, this::handleStencilPayloadUpdate));
        grid.setItems(actionsSupport.getAllStencils());
        return grid;
    }

    private StencilResponse gridSelection() {
        return stencilsGrid.asSingleSelect().getValue();
    }

    private void handleStencilCreate(StencilEditDialog dialog) {
        if (actionsSupport.createStencil(dialog)) {
            stencilsGrid.setItems(actionsSupport.getAllStencils());
            dialog.close();
        }
    }

    private void handleStencilUpdate(StencilEditDialog dialog) {
        if (actionsSupport.updateStencil(dialog)) {
            stencilsGrid.setItems(actionsSupport.getAllStencils());
            dialog.close();
        }
    }

    private void handleStencilPayloadUpdate(StencilPayloadEditDialog dialog) {
        if (actionsSupport.updateStencilPayload(dialog)) {
            stencilsGrid.setItems(actionsSupport.getAllStencils());
            dialog.close();
        }
    }
}
