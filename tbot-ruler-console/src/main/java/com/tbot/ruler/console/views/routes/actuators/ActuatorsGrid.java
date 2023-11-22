package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.views.components.EntityFilterableGrid;

import java.util.function.Consumer;

public class ActuatorsGrid extends EntityFilterableGrid<ActuatorModel, ActuatorsGridFilter> {

    public ActuatorsGrid(Consumer<ActuatorModel> selectHandler) {
        super(new ActuatorsGridFilter(), selectHandler);
        setUpColumns();
    }

    private void setUpColumns() {
        addFilterableColumn("Name", ActuatorModel::getName, gridFilter::setNameTerm);
        addFilterableColumn("Reference", ActuatorModel::getReference, gridFilter::setReferenceTerm);
        addFilterableColumn("UUID", ActuatorModel::getActuatorUuid, gridFilter::setUuidTerm);
        addFilterableColumn("Plugin", ActuatorModel::getPluginName, gridFilter::setPluginNameTerm);
        addFilterableColumn("Thing", ActuatorModel::getThingName, gridFilter::setPluginNameTerm);
    }
}
