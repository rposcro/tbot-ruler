package com.tbot.ruler.console.views.components;

import com.tbot.ruler.console.utils.BeanReader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import lombok.Builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EntityFilterableGrid<T> extends Grid<T> {

    private final BeanReader beanReader;
    private final EntityGridFilter<T> gridFilter;
    private final HeaderRow headerRow;

    private GridListDataView<T> dataView;
    private GridContextMenu<T> contextMenu;

    @Builder
    public EntityFilterableGrid(Class<?> beanType, String[] properties) {
        this.beanReader = new BeanReader(beanType, properties);
        this.gridFilter = new EntityGridFilter<>(beanReader, beanReader.getKnownProperties());

        getHeaderRows().clear();
        this.headerRow = appendHeaderRow();

        setSelectionMode(SelectionMode.SINGLE);
        setItems(Collections.emptyList());
        setSizeFull();
        addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);

        Arrays.stream(beanReader.getKnownProperties()).forEach(property -> {
            addFilterableColumn(
                    beanReader.getDisplayName(property),
                    bean -> beanReader.propertyAsString(bean, property),
                    filterValue -> gridFilter.setFilterValue(property, filterValue),
                    false);
        });
    }

    public void setSelectHandler(Consumer<T> selectHandler) {
        asSingleSelect().addValueChangeListener(event -> selectHandler.accept(event.getValue()));
    }

    public void addContextMenuAction(String name, Consumer<T> actionHandler) {
        contextMenu().addItem(name, event -> event.getItem().ifPresent(actionHandler::accept));
    }

    public void addContextMenuDivider() {
        contextMenu().add(new Hr());
    }

    public GridListDataView<T> setItems(List<T> items) {
        this.dataView = super.setItems(items);
        dataView.addFilter(gridFilter::test);
        return dataView;
    }

    private GridContextMenu<T> contextMenu() {
        if (contextMenu == null) {
            contextMenu = addContextMenu();
            contextMenu.addGridContextMenuOpenedListener(event -> {
                event.getItem().ifPresent(this::select);
            });
        }
        return contextMenu;
    }

    private Column<T> addFilterableColumn(
            String title,
            ValueProvider<T, ?> valueProvider,
            Consumer<String> filterValueListener,
            boolean filterHorizontal) {
        Column<T> column = addColumn(valueProvider)
                .setAutoWidth(true)
                .setSortable(true);
        headerRow.getCell(column)
                .setComponent(createFilteredHeader(title, filterValueListener, filterHorizontal));
        return column;
    }

    private Component createFilteredHeader(String headerTitle, Consumer<String> filterValueListener, boolean horizontal) {
        NativeLabel label = new NativeLabel(headerTitle);
        label.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        label.setWidthFull();
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

        if (horizontal) {
            HorizontalLayout layout = new HorizontalLayout(label, filterField);
            layout.setWidthFull();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            return layout;
        } else {
            VerticalLayout layout = new VerticalLayout(label, filterField);
            layout.setWidthFull();
            layout.getThemeList().clear();
            layout.getThemeList().add("spacing-xs");
            return layout;
        }
    }

    private void filterChanged() {
        dataView.refreshAll();
    }
}
