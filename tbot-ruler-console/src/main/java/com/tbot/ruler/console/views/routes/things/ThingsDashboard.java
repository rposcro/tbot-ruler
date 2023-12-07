package com.tbot.ruler.console.views.routes.things;

import com.tbot.ruler.console.accessors.ThingsAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.components.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route(value = "things", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Things Dashboard")
public class ThingsDashboard extends VerticalLayout {

    private final ThingActionsSupport actionsSupport;
    private final ThingsAccessor thingsAccessor;

    private final EntityPropertiesPanel<ThingResponse> thingPanel;
    private final ThingsGrid thingsGrid;

    @Autowired
    public ThingsDashboard(ThingsAccessor thingsAccessor, ThingActionsSupport actionsSupport) {
        this.thingsAccessor = thingsAccessor;
        this.actionsSupport = actionsSupport;
        this.thingPanel = constructThingPanel();
        this.thingsGrid = constructGrid();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Thing");
        createButton.addClickListener(event -> actionsSupport.launchThingCreate(this::handleThingCreate));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            content.add(thingsGrid, thingPanel);
            content.setFlexGrow(3, thingsGrid);
            content.setFlexGrow(1, thingPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading things ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private ThingsGrid constructGrid() {
        ThingsGrid grid = new ThingsGrid(thing -> thingPanel.applyToEntity(thing));
        grid.addContextMenuAction("Show Actuators", actionsSupport::launchShowActuators);
        grid.addContextMenuDivider();
        grid.addContextMenuAction("Edit", thingResponse -> actionsSupport.launchThingEdit(thingResponse, this::handleThingUpdate));
        grid.addContextMenuAction("Delete", actuatorModel -> actionsSupport.launchThingDelete(actuatorModel, this::handleThingDelete));
        grid.setItems(thingsAccessor.getAllThings());
        return grid;
    }

    private EntityPropertiesPanel<ThingResponse> constructThingPanel() {
        EntityPropertiesPanel<ThingResponse> panel = EntityPropertiesPanel.<ThingResponse>builder()
                .beanType(ThingResponse.class)
                .editHandler(() -> actionsSupport.launchThingEdit(
                                thingsGrid.asSingleSelect().getValue(), this::handleThingUpdate))
                .deleteHandler(() -> actionsSupport.launchThingDelete(
                        thingsGrid.asSingleSelect().getValue(), this::handleThingDelete))
                .properties(new String[] { "name", "thingUuid", "description", "configuration"} )
                .build();
        panel.getStyle().set("margin-top", "0px");
        return panel;
    }

    private void handleThingCreate(ThingEditDialog dialog) {
        if (actionsSupport.updateThing(dialog)) {
            thingsGrid.setItems(thingsAccessor.getAllThings());
            dialog.close();
        }
    }

    private void handleThingUpdate(ThingEditDialog dialog) {
        if (actionsSupport.createThing(dialog)) {
            thingsGrid.setItems(thingsAccessor.getAllThings());
            dialog.close();
        }
    }

    private void handleThingDelete(PromptDialog<ThingResponse> dialog) {
        if (actionsSupport.deleteThing(thingPanel.getCurrentEntity())) {
            thingsGrid.setItems(thingsAccessor.getAllThings());
            dialog.close();
        }
    }
}
