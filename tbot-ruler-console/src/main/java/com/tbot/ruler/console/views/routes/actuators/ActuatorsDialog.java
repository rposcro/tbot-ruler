package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.views.components.EntityGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.GridVariant;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

public class ActuatorsDialog extends Dialog {

    private final EntityGrid<ActuatorModel> actuatorsGrid;
    private final Button btnClose;

    private final List<ActuatorModel> actuators;

    @Builder
    @NonNull
    public ActuatorsDialog(String title, List<ActuatorModel> actuators) {
        this.actuators = actuators;
        this.btnClose = constructCloseButton();
        this.actuatorsGrid = constructActuatorsGrid();

        setHeaderTitle(title);
        setModal(true);
        setWidth("60%");
        setHeight("60%");

        add(actuatorsGrid);
        setUpFooter();
    }

    private void setUpFooter() {
        btnClose.getStyle().set("margin-left", "auto");
        btnClose.getStyle().set("margin-right", "auto");
        getFooter().add(btnClose);
    }

    private Button constructCloseButton() {
        Button button = new Button("Close");
        button.addClickListener(event -> this.close());
        return button;
    }

    private EntityGrid<ActuatorModel> constructActuatorsGrid() {
        EntityGrid<ActuatorModel> grid = new EntityGrid<>();
        grid.addColumn("Name", ActuatorModel::getName);
        grid.addColumn("Reference", ActuatorModel::getReference);
        grid.addColumn("UUID", ActuatorModel::getActuatorUuid);
        grid.addColumn("Plugin", ActuatorModel::getPluginName);

        grid.setSizeFull();
        grid.removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setItems(actuators);
        return grid;
    }
}
