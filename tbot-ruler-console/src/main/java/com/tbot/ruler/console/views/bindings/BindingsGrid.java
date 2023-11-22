package com.tbot.ruler.console.views.bindings;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BindingsGrid extends Grid<BindingModel> {

    private final HeaderRow headerRow;
    private final BindingsGridFilter gridFilter;

    private GridListDataView<BindingModel> dataView;

    public BindingsGrid() {
        this(binding -> {});
    }

    public BindingsGrid(Consumer<BindingModel> selectHandler) {
        this.headerRow = setUpHeader();
        this.gridFilter = new BindingsGridFilter();

        setItems(Collections.emptyList());
        setUpColumns();
        setSizeFull();
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        asSingleSelect().addValueChangeListener(
                event -> selectHandler.accept(event.getValue()));
    }

    public void setItems(List<BindingModel> items) {
        this.dataView = super.setItems(items);
        dataView.addFilter(gridFilter::test);
    }

    private void filterChanged() {
        dataView.refreshAll();
    }

    private HeaderRow setUpHeader() {
        getHeaderRows().clear();
        return this.appendHeaderRow();
    }

    private void setUpColumns() {
        setUpSenderNameColumn();
        setUpSenderTypeColumn();
        setUpSenderUuidColumn();
        setUpReceiverNameColumn();
        setUpReceiverUuidColumn();
    }

    private void setUpSenderNameColumn() {
        Column<BindingModel> column = addColumn(BindingModel::getSenderName)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Sender Name", gridFilter::setSenderNameTerm));
    }

    private void setUpSenderTypeColumn() {
        Column<BindingModel> column = addColumn(BindingModel::getSenderType)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Sender Type", gridFilter::setSenderTypeTerm));
    }

    private void setUpSenderUuidColumn() {
        Column<BindingModel> column = addColumn(BindingModel::getSenderUuid)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Sender UUID", gridFilter::setSenderUuidTerm));
    }

    private void setUpReceiverNameColumn() {
        Column<BindingModel> column = addColumn(BindingModel::getReceiverName)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Receiver Name", gridFilter::setReceiverNameTerm));
    }

    private void setUpReceiverUuidColumn() {
        Column<BindingModel> column = addColumn(BindingModel::getReceiverUuid)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Receiver UUID", gridFilter::setReceiverUuidTerm));
    }

    private Component createHeaderComponent(String headerTitle, Consumer<String> filterChangeConsumer) {
        NativeLabel label = new NativeLabel(headerTitle);
        label.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField filterField = new TextField();
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.setClearButtonVisible(true);
        filterField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        filterField.setWidthFull();
        filterField.getStyle().set("max-width", "100%");
        filterField.addValueChangeListener(e -> {
            filterChangeConsumer.accept(e.getValue());
            this.filterChanged();
        });
        VerticalLayout layout = new VerticalLayout(label, filterField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");
        return layout;
    }
}
