package com.tbot.ruler.console.views.actuators;

import com.tbot.ruler.console.views.AbstractFilterableGrid;

import java.util.function.Consumer;

public class ActuatorsGrid extends AbstractFilterableGrid<ActuatorModel, ActuatorsGridFilter> {

    public ActuatorsGrid(Consumer<ActuatorModel> selectHandler) {
        super(new ActuatorsGridFilter(), selectHandler);
        setUpColumns();
    }

    private void setUpColumns() {
        addFilteredColumn("Name", ActuatorModel::getName, gridFilter::setNameTerm);
        addFilteredColumn("Reference", ActuatorModel::getReference, gridFilter::setReferenceTerm);
        addFilteredColumn("UUID", ActuatorModel::getActuatorUuid, gridFilter::setUuidTerm);
        addFilteredColumn("Plugin", ActuatorModel::getPluginName, gridFilter::setPluginNameTerm);
        addFilteredColumn("Thing", ActuatorModel::getThingName, gridFilter::setPluginNameTerm);
    }
}
