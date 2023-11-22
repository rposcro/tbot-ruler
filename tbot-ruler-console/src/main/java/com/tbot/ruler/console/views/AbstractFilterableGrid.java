package com.tbot.ruler.console.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractFilterableGrid<T, F extends GridFilter<T>> extends AbstractGrid<T> {

    protected final F gridFilter;

    private GridListDataView<T> dataView;

    protected AbstractFilterableGrid(F gridFilter, Consumer<T> selectHandler) {
        super(selectHandler);
        this.gridFilter = gridFilter;
    }

    public void setItems(List<T> items) {
        this.dataView = super.setItems(items);
        dataView.addFilter(gridFilter::test);
    }

    protected Column<T> addFilteredColumn(
            String title,
            ValueProvider<T, ?> valueProvider,
            Consumer<String> filterValueListener) {
        Column<T> column = addColumn(valueProvider)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createFilteredHeader(title, filterValueListener));
        return column;
    }

    private Component createFilteredHeader(String headerTitle, Consumer<String> filterValueListener) {
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
            filterValueListener.accept(e.getValue());
            this.filterChanged();
        });
        VerticalLayout layout = new VerticalLayout(label, filterField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");
        return layout;
    }

    private void filterChanged() {
        dataView.refreshAll();
    }
}
