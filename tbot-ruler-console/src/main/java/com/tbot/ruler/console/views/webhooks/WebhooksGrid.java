package com.tbot.ruler.console.views.webhooks;

import com.tbot.ruler.controller.admin.payload.WebhookResponse;
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

public class WebhooksGrid extends Grid<WebhookResponse> {

    private final HeaderRow headerRow;
    private final WebhooksGridFilter gridFilter;

    private GridListDataView<WebhookResponse> dataView;

    public WebhooksGrid(Consumer<WebhookResponse> selectHandler) {
        this.headerRow = setUpHeader();
        this.gridFilter = new WebhooksGridFilter();

        setItems(Collections.emptyList());
        setUpColumns();
        setSizeFull();
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        asSingleSelect().addValueChangeListener(
                event -> selectHandler.accept(event.getValue()));
    }

    public void setItems(List<WebhookResponse> items) {
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
        setUpNameColumn();
        setUpOwnerColumn();
        setUpUuidColumn();
    }

    private void setUpNameColumn() {
        Column<WebhookResponse> column = addColumn(WebhookResponse::getName)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Name", gridFilter::setNameTerm));
    }

    private void setUpOwnerColumn() {
        Column<WebhookResponse> column = addColumn(WebhookResponse::getOwner)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("Owner", gridFilter::setOwnerTerm));
    }

    private void setUpUuidColumn() {
        Column<WebhookResponse> column = addColumn(WebhookResponse::getWebhookUuid)
                .setAutoWidth(true);
        headerRow.getCell(column)
                .setComponent(createHeaderComponent("UUID", gridFilter::setUuidTerm));
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
