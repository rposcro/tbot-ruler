package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.BindingsModelAccessor;
import com.tbot.ruler.console.accessors.model.BindingModel;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "bindings", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Bindings Dashboard")
public class BindingsDashboard extends VerticalLayout {

    private final BindingsModelAccessor dataSupport;
    private final BindingActionsSupport editSupport;

    private final BindingsGrid bindingsGrid;

    @Autowired
    public BindingsDashboard(BindingsModelAccessor dataSupport, BindingActionsSupport editSupport) {
        this.dataSupport = dataSupport;
        this.editSupport = editSupport;
        this.bindingsGrid = constructGrid();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Binding(s)");
        createButton.addClickListener(event -> editSupport.launchBindingCreate(
                this::handleBindingCreate, this::handleBindingsFinish));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            bindingsGrid.setItems(dataSupport.getAllBindingsModels());
            content.add(bindingsGrid);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading bindings ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private BindingsGrid constructGrid() {
        BindingsGrid grid = new BindingsGrid();
        grid.addContextMenuAction("Show All Senders", bindingModel -> editSupport.launchAllSendersDialog(bindingModel));
        grid.addContextMenuAction("Show All Receivers", bindingModel -> editSupport.launchAllReceiversDialog(bindingModel));
        grid.addContextMenuDivider();
        grid.addContextMenuAction("Delete Binding", bindingModel -> editSupport.launchBindingDelete(gridSelection(), this::handleBindingDelete));
        return grid;
    }

    private BindingModel gridSelection() {
        return bindingsGrid.asSingleSelect().getValue();
    }

    private void handleBindingDelete(PromptDialog<BindingModel> dialog) {
        if (editSupport.deleteBinding(dialog.getPromptedObject())) {
            dialog.close();
            bindingsGrid.setItems(dataSupport.getAllBindingsModels());
        }
    }

    private void handleBindingCreate(BindingsCreateDialog dialog) {
        String senderUuid = dialog.getSelectedSender().getUuid();
        String receiverUuid = dialog.getSelectedReceiver().getUuid();

        if (editSupport.createBinding(senderUuid, receiverUuid)) {
            dialog.addBinding(senderUuid, receiverUuid);
        }
    }

    private void handleBindingsFinish(BindingsCreateDialog dialog) {
        dialog.close();
        bindingsGrid.setItems(dataSupport.getAllBindingsModels());
    }
}
