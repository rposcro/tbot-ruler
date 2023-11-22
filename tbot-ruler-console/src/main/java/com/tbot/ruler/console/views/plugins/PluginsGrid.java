package com.tbot.ruler.console.views.plugins;

import com.tbot.ruler.console.views.AbstractGridPanel;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.vaadin.flow.component.grid.GridVariant;

import java.util.Collections;
import java.util.function.Consumer;

public class PluginsGrid extends AbstractGridPanel<PluginResponse> {

    public PluginsGrid(Consumer<PluginResponse> selectHandler) {
        setItems(Collections.emptyList());
        setUpColumns();
        setSizeFull();
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        asSingleSelect().addValueChangeListener(
                event -> selectHandler.accept(event.getValue()));
    }

    private void setUpColumns() {
        addColumn("Name", PluginResponse::getName);
        addColumn("UUID", PluginResponse::getPluginUuid);
        addColumn("Factory", PluginResponse::getFactoryClass);
    }
}
