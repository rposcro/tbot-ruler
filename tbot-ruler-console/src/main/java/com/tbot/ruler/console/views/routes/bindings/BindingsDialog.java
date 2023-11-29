package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.views.components.EntityGrid;
import com.tbot.ruler.console.accessors.model.BindingModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Builder;

import java.util.List;

public class BindingsDialog extends Dialog {

    private final EntityGrid<BindingModel> inboundGrid;
    private final EntityGrid<BindingModel> outboundGrid;

    @Builder
    public BindingsDialog(String title, List<BindingModel> inboundBindings, List<BindingModel> outboundBindings) {
        this.inboundGrid = constructInboundGrid(inboundBindings);
        this.outboundGrid = constructOutboundGrid(outboundBindings);

        setHeaderTitle(title);
        setModal(true);
        setWidth("60%");
        setHeight("60%");

        add(constructContent());
        setUpFooter();
    }

    private Component constructContent() {
        VerticalLayout layout = new VerticalLayout();
        if (inboundGrid != null) {
            layout.add(new NativeLabel("Inbound Bindings"));
            layout.add(inboundGrid);
        }
        if (outboundGrid != null) {
            layout.add(new NativeLabel("Outbound Bindings"));
            layout.add(outboundGrid);
        }
        layout.setSizeFull();
        return layout;
    }

    private Details constructInboundDetails(List<BindingModel> bindings) {
        EntityGrid<BindingModel> grid = constructInboundGrid(bindings);
        Details details = new Details("Inbound Bindings", grid);
        details.setSizeFull();
        details.setOpened(true);
        return details;
    }

    private Details constructOutboundDetails(List<BindingModel> bindings) {
        EntityGrid<BindingModel> grid = constructOutboundGrid(bindings);
        Details details = new Details("Outbound Bindings", grid);
        details.setSizeFull();
        details.setOpened(true);
        return details;
    }

    private EntityGrid<BindingModel> constructInboundGrid(List<BindingModel> bindings) {
        if (bindings == null) {
            return null;
        }
        EntityGrid<BindingModel> grid = new EntityGrid<>();
        grid.addColumn("Sender Name", BindingModel::getSenderName);
        grid.addColumn("Sender UUID", BindingModel::getSenderUuid);
        grid.addColumn("Sender Type", BindingModel::getSenderType);
        grid.setSizeFull();
        grid.setItems(bindings);
        return grid;
    }

    private EntityGrid<BindingModel> constructOutboundGrid(List<BindingModel> bindings) {
        if (bindings == null) {
            return null;
        }
        EntityGrid<BindingModel> grid = new EntityGrid<>();
        grid.addColumn("Receiver Name", BindingModel::getReceiverName);
        grid.addColumn("Receiver UUID", BindingModel::getReceiverUuid);
        grid.setSizeFull();
        grid.setItems(bindings);
        return grid;
    }

    private void setUpFooter() {
        Button btnClose = new Button("Close");
        btnClose.addClickListener(event -> this.close());
        btnClose.getStyle().set("margin-left", "auto");
        btnClose.getStyle().set("margin-right", "auto");
        getFooter().add(btnClose);
    }
}
