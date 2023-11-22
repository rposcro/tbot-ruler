package com.tbot.ruler.console.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.function.ValueProvider;

public abstract class AbstractGridPanel<T> extends Grid<T> {

    protected final HeaderRow headerRow;

    protected AbstractGridPanel() {
        getHeaderRows().clear();
        this.headerRow = appendHeaderRow();
    }

    protected Column<?> addColumn(String title, ValueProvider<T, ?> valueProvider) {
        Column<T> column = addColumn(valueProvider)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createHeader(title));
        return column;
    }

    private Component createHeader(String headerTitle) {
        NativeLabel label = new NativeLabel(headerTitle);
        label.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        return new Div(label);
    }
}
