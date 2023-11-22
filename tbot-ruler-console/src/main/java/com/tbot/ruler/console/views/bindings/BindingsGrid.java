package com.tbot.ruler.console.views.bindings;

import com.tbot.ruler.console.views.AbstractFilterableGrid;

public class BindingsGrid extends AbstractFilterableGrid<BindingModel, BindingsGridFilter> {

    public BindingsGrid() {
        super(new BindingsGridFilter(), binding -> {});
        setUpColumns();
    }

    private void setUpColumns() {
        addFilteredColumn("Sender Name", BindingModel::getSenderName, gridFilter::setSenderNameTerm);
        addFilteredColumn("Sender UUID", BindingModel::getSenderUuid, gridFilter::setSenderUuidTerm);
        addFilteredColumn("Receiver Name", BindingModel::getReceiverName, gridFilter::setReceiverNameTerm);
        addFilteredColumn("Receiver UUID", BindingModel::getReceiverUuid, gridFilter::setReceiverUuidTerm);
    }
}
