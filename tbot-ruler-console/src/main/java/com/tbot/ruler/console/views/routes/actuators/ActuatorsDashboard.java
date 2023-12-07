package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.ActuatorsModelAccessor;
import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.components.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "actuators", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Actuators Dashboard")
public class ActuatorsDashboard extends VerticalLayout {

    private final ActuatorActionsSupport actionsSupport;
    private final ActuatorsModelAccessor dataSupport;

    private final EntityPropertiesPanel<ActuatorModel> actuatorPanel;
    private final ActuatorsGrid actuatorsGrid;

    @Autowired
    public ActuatorsDashboard(ActuatorActionsSupport actionsSupport, ActuatorsModelAccessor dataSupport) {
        this.actionsSupport = actionsSupport;
        this.dataSupport = dataSupport;
        this.actuatorsGrid = constructGrid();
        this.actuatorPanel = constructItemPanel();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    public void selectActuator(String actuatorUuid) {
        if (actuatorUuid != null) {
            DataCommunicator<ActuatorModel> dataCommunicator = actuatorsGrid.getDataCommunicator();
            for (int idx = 0; idx < dataCommunicator.getItemCount(); idx++) {
                if (dataCommunicator.getItem(idx).getActuatorUuid().equals(actuatorUuid)) {
                    actuatorsGrid.select(dataCommunicator.getItem(idx));
                    actuatorsGrid.scrollToIndex(idx);
                    return;
                }
            }
        }
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Actuator");
        createButton.addClickListener(event -> actionsSupport.launchActuatorCreate(this::handleCreateActuator));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            content.add(actuatorsGrid, actuatorPanel);
            content.setFlexGrow(3, actuatorsGrid);
            content.setFlexGrow(1, actuatorPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading actuators ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private EntityPropertiesPanel<ActuatorModel> constructItemPanel() {
        EntityPropertiesPanel<ActuatorModel> panel = EntityPropertiesPanel.<ActuatorModel>builder()
                .beanType(ActuatorModel.class)
                .editHandler(() -> actionsSupport.launchActuatorEdit(
                        actuatorsGrid.asSingleSelect().getValue(), this::handleUpdateActuator))
                .deleteHandler(() -> actionsSupport.launchActuatorDelete(
                        actuatorsGrid.asSingleSelect().getValue(), this::handleDeleteActuator))
                .properties(new String[] { "name", "reference", "actuatorUuid", "pluginName", "thingName", "description", "configuration"} )
                .build();
        panel.getStyle().set("margin-top", "0px");
        return panel;
    }

    private ActuatorsGrid constructGrid() {
        ActuatorsGrid grid = new ActuatorsGrid();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addContextMenuAction("Show Bindings", actuatorModel -> actionsSupport.launchShowBindings(actuatorModel));
        grid.addContextMenuDivider();
        grid.addContextMenuAction("Edit", actuatorModel -> actionsSupport.launchActuatorEdit(actuatorModel, this::handleUpdateActuator));
        grid.addContextMenuAction("Delete", actuatorModel -> actionsSupport.launchActuatorDelete(actuatorModel, this::handleDeleteActuator));
        grid.setSelectHandler(actuatorModel -> actuatorPanel.applyToEntity(actuatorModel));
        grid.setItems(dataSupport.getAllActuatorsModels());
        return grid;
    }

    private void handleUpdateActuator(ActuatorEditDialog dialog) {
        if (actionsSupport.updateActuator(dialog)) {
            actuatorsGrid.setItems(dataSupport.getAllActuatorsModels());
            dialog.close();
        }
    }

    private void handleCreateActuator(ActuatorEditDialog dialog) {
        if (actionsSupport.createActuator(dialog)) {
            actuatorsGrid.setItems(dataSupport.getAllActuatorsModels());
            dialog.close();
        }
    }

    private void handleDeleteActuator(PromptDialog<ActuatorModel> deleteDialog) {
        if (actionsSupport.deleteActuator(actuatorPanel.getCurrentEntity())) {
            actuatorsGrid.setItems(dataSupport.getAllActuatorsModels());
            deleteDialog.close();
        }
    }
}
