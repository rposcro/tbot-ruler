package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.ActuatorsModelAccessor;
import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.components.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "actuators", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Actuators Dashboard")
public class ActuatorsDashboard extends VerticalLayout {

    private final ActuatorEditSupport editSupport;
    private final ActuatorsModelAccessor dataSupport;

    private final EntityPropertiesPanel<ActuatorModel> actuatorPanel;
    private final ActuatorsGrid actuatorsGrid;

    @Autowired
    public ActuatorsDashboard(ActuatorEditSupport editSupport, ActuatorsModelAccessor dataSupport) {
        this.editSupport = editSupport;
        this.dataSupport = dataSupport;
        this.actuatorsGrid = constructGrid();
        this.actuatorPanel = EntityPropertiesPanel.<ActuatorModel>builder()
                .beanType(ActuatorModel.class)
                .editHandler(() -> editSupport.launchActuatorEdit(
                        actuatorsGrid.asSingleSelect().getValue(), this::handleUpdateActuator))
                .properties(new String[] { "name", "reference", "actuatorUuid", "pluginName", "thingName", "description", "configuration"} )
                .build();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Actuator");
        createButton.addClickListener(event -> editSupport.launchActuatorCreate(this::handleCreateActuator));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            actuatorsGrid.setItems(dataSupport.getAllActuatorsModels());
            actuatorPanel.getStyle().set("margin-top", "0px");
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

    private ActuatorsGrid constructGrid() {
        ActuatorsGrid grid = new ActuatorsGrid();
        grid.addContextMenuAction("Bindings", actuatorModel -> editSupport.launchBindingsDialog(actuatorModel));
        grid.setSelectHandler(actuatorModel -> actuatorPanel.applyToEntity(actuatorModel));
        return grid;
    }

    private void handleUpdateActuator(ActuatorEditDialog dialog) {
        if (editSupport.updateActuator(dialog)) {
            actuatorsGrid.setItems(dataSupport.getAllActuatorsModels());
            dialog.close();
        }
    }

    private void handleCreateActuator(ActuatorEditDialog dialog) {
        if (editSupport.createActuator(dialog)) {
            actuatorsGrid.setItems(dataSupport.getAllActuatorsModels());
            dialog.close();
        }
    }
}
