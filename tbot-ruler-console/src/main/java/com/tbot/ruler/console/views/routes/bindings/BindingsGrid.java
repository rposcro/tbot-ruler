package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.model.BindingModel;
import com.tbot.ruler.console.views.components.EntityFilterableGrid;

public class BindingsGrid extends EntityFilterableGrid<BindingModel> {

    public BindingsGrid() {
        super(BindingModel.class, new String[] { "senderName", "senderUuid", "receiverName", "receiverUuid" });
    }
}
