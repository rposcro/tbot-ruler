package com.tbot.ruler.console.views.things;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.clients.ThingsClient;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.EntityPropertiesPanel;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.controller.admin.payload.ThingCreateRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.ThingUpdateRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Route(value = "things", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Things Dashboard")
public class ThingsDashboard extends VerticalLayout {

    private final ThingsClient thingsClient;
    private final PopupNotifier popupNotifier;

    private final ObjectMapper objectMapper;
    private final EntityPropertiesPanel<ThingResponse> thingPanel;
    private final Grid<ThingResponse> thingsGrid;

    @Autowired
    public ThingsDashboard(ThingsClient thingsClient, PopupNotifier popupNotifier) {
        this.thingsClient = thingsClient;
        this.popupNotifier = popupNotifier;
        this.objectMapper = new ObjectMapper();
        this.thingPanel = EntityPropertiesPanel.<ThingResponse>builder()
                .beanType(ThingResponse.class)
                .editHandler(() -> handleEditRequest(true))
                .properties(new String[] { "name", "thingUuid", "description", "configuration"} )
                .build();
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
            List<ThingResponse> things = thingsClient.getAllThings();
            thingsGrid.setItems(things);
            thingPanel.getStyle().set("margin-top", "0px");
            content.add(thingsGrid, thingPanel);
            content.setFlexGrow(3, thingsGrid);
            content.setFlexGrow(1, thingPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading things ..."),
                    new Span(e.getMessage())));
        }

        content.setWidthFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private Grid<ThingResponse> constructGrid() {
        Grid<ThingResponse> grid = new Grid<>();
        setUpColumn(grid, ThingResponse::getName, "Name").setSortable(true);
        setUpColumn(grid, ThingResponse::getThingUuid, "UUID");
        setUpColumn(grid, ThingResponse::getDescription, "Description");

        grid.setWidthFull();
        grid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.asSingleSelect().addValueChangeListener(
                event -> thingPanel.applyToEntity(event.getValue()));
        return grid;
    }

    private Grid.Column<ThingResponse> setUpColumn(Grid<ThingResponse> grid, Function<ThingResponse, ?> valueProvider, String headerTitle) {
        return grid.addColumn(thing -> valueProvider.apply(thing))
                .setAutoWidth(true)
                .setHeader(headerTitle);
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
        thingsClient.updateThing(thingUuid, request);
        thingsGrid.setItems(thingsClient.getAllThings());
    }

    private void createThing(ThingEditDialog dialog) throws Exception {
        JsonNode configuration = objectMapper.readTree(dialog.getThingDescription());
        ThingCreateRequest request = ThingCreateRequest.builder()
                .name(dialog.getThingName())
                .description(dialog.getThingDescription())
                .configuration(configuration)
                .build();
        thingsClient.createThing(request);
        thingsGrid.setItems(thingsClient.getAllThings());
    }
}
