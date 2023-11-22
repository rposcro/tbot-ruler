package com.tbot.ruler.console.views.plugins;

import com.tbot.ruler.console.views.AbstractGrid;
import com.tbot.ruler.controller.admin.payload.PluginResponse;

import java.util.function.Consumer;

public class PluginsGrid extends AbstractGrid<PluginResponse> {

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
