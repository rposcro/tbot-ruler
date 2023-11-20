package com.tbot.ruler.console.views.actuators;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

@Route(value = "actuators", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Actuators Dashboard")
public class ActuatorsDashboard extends VerticalLayout {

    private final ActuatorEditSupport editSupport;

    private final EntityPropertiesPanel<ActuatorModel> actuatorPanel;
    private final Grid<ActuatorModel> actuatorsGrid;

    @Autowired
    public ActuatorsDashboard(ActuatorEditSupport editSupport) {
        this.editSupport = editSupport;
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
            actuatorsGrid.setItems(editSupport.fetchAllActuatorsModels());
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

    private Grid<ActuatorModel> constructGrid() {
        Grid<ActuatorModel> grid = new Grid<>();
        setUpColumn(grid, ActuatorModel::getName, "Name").setSortable(true);
        setUpColumn(grid, ActuatorModel::getReference, "Reference").setSortable(true);
        setUpColumn(grid, ActuatorModel::getActuatorUuid, "UUID");
        setUpColumn(grid, ActuatorModel::getPluginName, "Plugin").setSortable(true);
        setUpColumn(grid, ActuatorModel::getThingName, "Thing").setSortable(true);

        grid.setSizeFull();
        grid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.asSingleSelect().addValueChangeListener(
                event -> actuatorPanel.applyToEntity(event.getValue()));
        return grid;
    }

    private Grid.Column<ActuatorModel> setUpColumn(Grid<ActuatorModel> grid, Function<ActuatorModel, ?> valueProvider, String headerTitle) {
        return grid.addColumn(actuator -> valueProvider.apply(actuator))
                .setAutoWidth(true)
                .setHeader(headerTitle);
    }

    private void handleUpdateActuator(ActuatorEditDialog dialog) {
        if (editSupport.updateActuator(dialog)) {
            actuatorsGrid.setItems(editSupport.fetchAllActuatorsModels());
            dialog.close();
        }
    }

    private void handleCreateActuator(ActuatorEditDialog dialog) {
        if (editSupport.createActuator(dialog)) {
            actuatorsGrid.setItems(editSupport.fetchAllActuatorsModels());
            dialog.close();
        }
    }
}
