package com.tbot.ruler.console.views.routes.stencils;

import com.tbot.ruler.console.views.components.EntityFilterableGrid;
import com.tbot.ruler.controller.admin.payload.StencilResponse;

public class StencilsGrid extends EntityFilterableGrid<StencilResponse> {

    public StencilsGrid() {
        super(StencilResponse.class, new String[] { "owner", "type", "stencilUuid"});
    }
}
