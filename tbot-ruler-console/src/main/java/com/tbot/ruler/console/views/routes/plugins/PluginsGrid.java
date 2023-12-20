package com.tbot.ruler.console.views.routes.plugins;

import com.tbot.ruler.console.views.components.EntityGrid;
import com.tbot.ruler.controller.admin.payload.PluginResponse;

import java.util.function.Consumer;

public class PluginsGrid extends EntityGrid<PluginResponse> {

    public PluginsGrid(Consumer<PluginResponse> selectHandler) {
        super(selectHandler);
        setUpColumns();
    }

    private void setUpColumns() {
        addColumn("Name", PluginResponse::getName);
        addColumn("UUID", PluginResponse::getPluginUuid);
        addColumn("Factory", PluginResponse::getFactoryClass);
    }
}
