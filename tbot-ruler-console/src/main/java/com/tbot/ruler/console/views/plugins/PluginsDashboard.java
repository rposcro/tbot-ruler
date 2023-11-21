package com.tbot.ruler.console.views.plugins;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.clients.PluginsClient;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.EntityPropertiesPanel;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Route(value = "plugins", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Plugins Dashboard")
public class PluginsDashboard extends VerticalLayout {

    private final PluginsClient pluginsClient;
    private final PopupNotifier popupNotifier;

    private final ObjectMapper objectMapper;
    private final EntityPropertiesPanel<PluginResponse> pluginPanel;
    private final Grid<PluginResponse> pluginsGrid;

    @Autowired
    public PluginsDashboard(PluginsClient pluginsClient, PopupNotifier popupNotifier) {
        this.pluginsClient = pluginsClient;
        this.popupNotifier = popupNotifier;
        this.objectMapper = new ObjectMapper();
        this.pluginPanel = EntityPropertiesPanel.<PluginResponse>builder()
                .beanType(PluginResponse.class)
                .editHandler(() -> handleEditRequest(true))
                .properties(new String[] { "name", "pluginUuid", "factoryClass", "configuration"} )
                .build();
        this.pluginsGrid = constructGrid();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Plugin");
        createButton.addClickListener(event -> handleEditRequest(false));

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            List<PluginResponse> plugins = pluginsClient.getAllPlugins();
            pluginsGrid.setItems(plugins);
            pluginPanel.getStyle().set("margin-top", "0px");
            content.add(pluginsGrid, pluginPanel);
            content.setFlexGrow(3, pluginsGrid);
            content.setFlexGrow(1, pluginPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading plugins ..."),
                    new Span(e.getMessage())));
        }

        content.setWidthFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private Grid<PluginResponse> constructGrid() {
        Grid<PluginResponse> grid = new Grid<>();
        setUpColumn(grid, PluginResponse::getName, "Name").setSortable(true);
        setUpColumn(grid, PluginResponse::getPluginUuid, "UUID");
        setUpColumn(grid, PluginResponse::getFactoryClass, "Factory").setSortable(true);

        grid.setWidthFull();
        grid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.asSingleSelect().addValueChangeListener(
                event -> pluginPanel.applyToEntity(event.getValue()));
        return grid;
    }

    private Grid.Column<PluginResponse> setUpColumn(Grid<PluginResponse> grid, Function<PluginResponse, ?> valueProvider, String headerTitle) {
        return grid.addColumn(plugin -> valueProvider.apply(plugin))
                .setAutoWidth(true)
                .setHeader(headerTitle);
    }

    private void handleEditRequest(boolean updateMode) {
        try {
            PluginEditDialog dialog;

            if (updateMode) {
                PluginResponse plugin = pluginsGrid.asSingleSelect().getValue();
                dialog = PluginEditDialog.builder()
                        .updateMode(true)
                        .factories(pluginsClient.getFactories())
                        .submitHandler(this::handlePluginUpdate)
                        .original(plugin)
                        .build();
            } else {
                dialog = PluginEditDialog.builder()
                        .updateMode(false)
                        .factories(pluginsClient.getFactories())
                        .submitHandler(this::handlePluginUpdate)
                        .build();
            }

            dialog.open();
        } catch(Exception e) {
            log.error("WTF", e);
        }
    }

    private void handlePluginUpdate(PluginEditDialog dialog) {
        log.info("Plugin update requested!");
        dialog.close();

        try {
            if (dialog.isUpdateMode()) {
                updatePlugin(dialog);
            } else {
                createPlugin(dialog);
            }
        } catch(JsonMappingException e) {
            popupNotifier.notifyError("Configuration is not parsable!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
        }
    }

    private void updatePlugin(PluginEditDialog dialog) throws Exception {
        String pluginUuid = pluginsGrid.asSingleSelect().getValue().getPluginUuid();
        JsonNode configuration = objectMapper.readTree(dialog.getPluginConfiguration());
        PluginUpdateRequest request = PluginUpdateRequest.builder()
                .name(dialog.getPluginName())
                .configuration(configuration)
                .build();
        pluginsClient.updatePlugin(pluginUuid, request);
        pluginsGrid.setItems(pluginsClient.getAllPlugins());
    }

    private void createPlugin(PluginEditDialog dialog) throws Exception {
        JsonNode configuration = objectMapper.readTree(dialog.getPluginConfiguration());
        PluginCreateRequest request = PluginCreateRequest.builder()
                .name(dialog.getPluginName())
                .factoryClass(dialog.getFactory())
                .configuration(configuration)
                .build();
        pluginsClient.createPlugin(request);
        pluginsGrid.setItems(pluginsClient.getAllPlugins());
    }
}
