package com.tbot.ruler.console.views.things;

import com.tbot.ruler.console.views.AbstractGrid;
import com.tbot.ruler.controller.admin.payload.ThingResponse;

import java.util.function.Consumer;

public class ThingsGrid extends AbstractGrid<ThingResponse> {

    public ThingsGrid(Consumer<ThingResponse> selectHandler) {
        super(selectHandler);
        setUpColumns();
    }

    private void setUpColumns() {
        addColumn("Name", ThingResponse::getName);
        addColumn("UUID", ThingResponse::getThingUuid);
        addColumn("Factory", ThingResponse::getDescription);
    }
}
