package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.views.components.EntityFilterableGrid;

public class BindingsGrid extends EntityFilterableGrid<BindingModel, BindingsGridFilter> {

    public BindingsGrid() {
        super(new BindingsGridFilter(), binding -> {});
        setUpColumns();
    }

    private void setUpColumns() {
        addFilterableColumn("Sender Name", BindingModel::getSenderName, gridFilter::setSenderNameTerm);
        addFilterableColumn("Sender UUID", BindingModel::getSenderUuid, gridFilter::setSenderUuidTerm);
        addFilterableColumn("Receiver Name", BindingModel::getReceiverName, gridFilter::setReceiverNameTerm);
        addFilterableColumn("Receiver UUID", BindingModel::getReceiverUuid, gridFilter::setReceiverUuidTerm);
    }
}
