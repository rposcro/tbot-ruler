package com.tbot.ruler.console.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.function.ValueProvider;

import java.util.Collections;
import java.util.function.Consumer;

public class EntityGrid<T> extends Grid<T> {

    protected final HeaderRow headerRow;

    public EntityGrid() {
        this(null);
    }

    public EntityGrid(Consumer<T> selectHandler) {
        getHeaderRows().clear();
        this.headerRow = appendHeaderRow();

        setItems(Collections.emptyList());
        setSizeFull();
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);

        if (selectHandler != null) {
            asSingleSelect().addValueChangeListener(event -> selectHandler.accept(event.getValue()));
        }
    }

    public Column<?> addColumn(String title, ValueProvider<T, ?> valueProvider) {
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
