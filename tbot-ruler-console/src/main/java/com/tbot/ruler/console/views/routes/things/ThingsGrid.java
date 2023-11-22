package com.tbot.ruler.console.views.routes.things;

import com.tbot.ruler.console.views.components.EntityGrid;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;

import java.util.function.Consumer;

public class ThingsGrid extends EntityGrid<ThingResponse> {

    private GridContextMenu<ThingResponse> contextMenu;

    public ThingsGrid(Consumer<ThingResponse> selectHandler) {
        super(selectHandler);
        setUpColumns();
    }

    public void addContextAction(String name, Consumer<ThingResponse> actionHandler) {
        if (contextMenu == null) {
            contextMenu = addContextMenu();
        }
        contextMenu.addItem(name, event -> this.contextActionListener(event, actionHandler));
    }

    private void contextActionListener(
            GridContextMenu.GridContextMenuItemClickEvent<ThingResponse> event,
            Consumer<ThingResponse> actionHandler) {
        event.getItem().ifPresent(actionHandler::accept);
    }

    private void setUpColumns() {
        addColumn("Name", ThingResponse::getName);
        addColumn("UUID", ThingResponse::getThingUuid);
        addColumn("Factory", ThingResponse::getDescription);
    }
}
