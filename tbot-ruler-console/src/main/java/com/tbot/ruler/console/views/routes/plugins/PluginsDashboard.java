package com.tbot.ruler.console.views.routes.plugins;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.accessors.PluginsAccessor;
import com.tbot.ruler.console.views.components.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyError;

@Slf4j
@Route(value = "plugins", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Plugins Dashboard")
public class PluginsDashboard extends VerticalLayout {

    private final PluginActionSupport pluginActionSupport;
    private final PluginsAccessor pluginsAccessor;

    private final ObjectMapper objectMapper;
    private final EntityPropertiesPanel<PluginResponse> pluginPanel;
    private final PluginsGrid pluginsGrid;

    @Autowired
    public PluginsDashboard(PluginsAccessor pluginsAccessor, PluginActionSupport pluginActionSupport) {
        this.pluginsAccessor = pluginsAccessor;
        this.pluginActionSupport = pluginActionSupport;
        this.objectMapper = new ObjectMapper();
        this.pluginPanel = constructPluginPanel();
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
            content.add(pluginsGrid, pluginPanel);
            content.setFlexGrow(3, pluginsGrid);
            content.setFlexGrow(1, pluginPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading plugins ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private EntityPropertiesPanel<PluginResponse> constructPluginPanel() {
        EntityPropertiesPanel<PluginResponse> panel = EntityPropertiesPanel.<PluginResponse>builder()
                .beanType(PluginResponse.class)
                .editHandler(() -> handleEditRequest(true))
                .properties(new String[] { "name", "pluginUuid", "factoryClass", "configuration"} )
                .build();
        panel.getStyle().set("margin-top", "0px");
        return panel;
    }

    private PluginsGrid constructGrid() {
        PluginsGrid grid = new PluginsGrid(plugin -> pluginPanel.applyToEntity(plugin));
        grid.addContextAction("Show Actuators", pluginActionSupport::openActuatorsDialog);
        grid.setItems(pluginsAccessor.getAllPlugins());
        return grid;
    }

    private void handleEditRequest(boolean updateMode) {
        try {
            PluginEditDialog dialog;

            if (updateMode) {
                PluginResponse plugin = pluginsGrid.asSingleSelect().getValue();
                dialog = PluginEditDialog.builder()
                        .updateMode(true)
                        .factories(pluginsAccessor.getAvailableFactories())
                        .submitHandler(this::handlePluginUpdate)
                        .original(plugin)
                        .build();
            } else {
                dialog = PluginEditDialog.builder()
                        .updateMode(false)
                        .factories(pluginsAccessor.getAvailableFactories())
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
            notifyError("Configuration is not parsable!");
        } catch(Exception e) {
            notifyError("Something's wrong!");
        }
    }

    private void updatePlugin(PluginEditDialog dialog) throws Exception {
        String pluginUuid = pluginsGrid.asSingleSelect().getValue().getPluginUuid();
        JsonNode configuration = objectMapper.readTree(dialog.getPluginConfiguration());
        PluginUpdateRequest request = PluginUpdateRequest.builder()
                .name(dialog.getPluginName())
                .configuration(configuration)
                .build();
        pluginsAccessor.updatePlugin(pluginUuid, request);
        pluginsGrid.setItems(pluginsAccessor.getAllPlugins());
    }

    private void createPlugin(PluginEditDialog dialog) throws Exception {
        JsonNode configuration = objectMapper.readTree(dialog.getPluginConfiguration());
        PluginCreateRequest request = PluginCreateRequest.builder()
                .name(dialog.getPluginName())
                .factoryClass(dialog.getFactory())
                .configuration(configuration)
                .build();
        pluginsAccessor.createPlugin(request);
        pluginsGrid.setItems(pluginsAccessor.getAllPlugins());
    }
}
