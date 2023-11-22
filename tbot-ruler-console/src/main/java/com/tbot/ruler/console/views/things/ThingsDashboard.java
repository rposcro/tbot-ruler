package com.tbot.ruler.console.views.things;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.accessors.RouteThingsAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.EntityPropertiesPanel;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.controller.admin.payload.ThingCreateRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.ThingUpdateRequest;
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

    private final RouteThingsAccessor thingsAccessor;
    private final PopupNotifier popupNotifier;

    private final ObjectMapper objectMapper;
    private final EntityPropertiesPanel<ThingResponse> thingPanel;
    private final ThingsGrid thingsGrid;

    @Autowired
    public ThingsDashboard(RouteThingsAccessor thingsAccessor, PopupNotifier popupNotifier) {
        this.thingsAccessor = thingsAccessor;
        this.popupNotifier = popupNotifier;
        this.objectMapper = new ObjectMapper();
        this.thingPanel = constructThingPanel();
        this.thingsGrid = constructGrid();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Thing");
        createButton.addClickListener(event -> handleEditRequest(false));

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
        grid.setItems(thingsAccessor.getAllThings());
        return grid;
    }

    private EntityPropertiesPanel<ThingResponse> constructThingPanel() {
        EntityPropertiesPanel<ThingResponse> panel = EntityPropertiesPanel.<ThingResponse>builder()
                .beanType(ThingResponse.class)
                .editHandler(() -> handleEditRequest(true))
                .properties(new String[] { "name", "thingUuid", "description", "configuration"} )
                .build();
        panel.getStyle().set("margin-top", "0px");
        return panel;
    }

    private void handleEditRequest(boolean updateMode) {
        try {
            ThingEditDialog dialog;

            if (updateMode) {
                ThingResponse thing = thingsGrid.asSingleSelect().getValue();
                String configuration = thing.getConfiguration() == null || thing.getConfiguration().isNull() ?
                        null : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(thing.getConfiguration());
                dialog = ThingEditDialog.builder()
                        .updateMode(true)
                        .submitHandler(this::handleThingUpdate)
                        .original(thing)
                        .build();
            } else {
                dialog = ThingEditDialog.builder()
                        .updateMode(false)
                        .submitHandler(this::handleThingUpdate)
                        .build();
            }

            dialog.open();
        } catch(Exception e) {
            log.error("WTF", e);
        }
    }

    private void handleThingUpdate(ThingEditDialog dialog) {
        log.info("Thing update requested!");
        dialog.close();

        try {
            if (dialog.isUpdateMode()) {
                updateThing(dialog);
            } else {
                createThing(dialog);
            }
        } catch(JsonMappingException e) {
            popupNotifier.notifyError("Configuration is not parsable!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
            log.error("Something went wrong when saving thing", e);
        }
    }

    private void updateThing(ThingEditDialog dialog) throws Exception {
        String thingUuid = thingsGrid.asSingleSelect().getValue().getThingUuid();
        JsonNode configuration = objectMapper.readTree(dialog.getThingConfiguration());
        ThingUpdateRequest request = ThingUpdateRequest.builder()
                .name(dialog.getThingName())
                .description(dialog.getThingDescription())
                .configuration(configuration)
                .build();
        thingsAccessor.updateThing(thingUuid, request);
        thingsGrid.setItems(thingsAccessor.getAllThings());
    }

    private void createThing(ThingEditDialog dialog) throws Exception {
        JsonNode configuration = objectMapper.readTree(dialog.getThingDescription());
        ThingCreateRequest request = ThingCreateRequest.builder()
                .name(dialog.getThingName())
                .description(dialog.getThingDescription())
                .configuration(configuration)
                .build();
        thingsAccessor.createThing(request);
        thingsGrid.setItems(thingsAccessor.getAllThings());
    }
}
