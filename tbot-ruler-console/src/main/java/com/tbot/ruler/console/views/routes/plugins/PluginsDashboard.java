package com.tbot.ruler.console.views.routes.plugins;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.accessors.PluginsAccessor;
import com.tbot.ruler.console.views.components.EntityPropertiesPanel;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route(value = "plugins", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Plugins Dashboard")
public class PluginsDashboard extends VerticalLayout {

    private final PluginActionsSupport actionsSupport;
    private final PluginsAccessor pluginsAccessor;

    private final EntityPropertiesPanel<PluginResponse> pluginPanel;
    private final PluginsGrid pluginsGrid;

    @Autowired
    public PluginsDashboard(PluginsAccessor pluginsAccessor, PluginActionsSupport actionsSupport) {
        this.pluginsAccessor = pluginsAccessor;
        this.actionsSupport = actionsSupport;
        this.pluginPanel = constructPluginPanel();
        this.pluginsGrid = constructGrid();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Plugin");
        createButton.addClickListener(event -> actionsSupport.launchPluginCreate(this::handlePluginCreate));

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
                .editHandler(() -> actionsSupport.launchPluginEdit(
                        pluginsGrid.asSingleSelect().getValue(), this::handlePluginUpdate))
                .deleteHandler(() -> actionsSupport.launchPluginDelete(
                        pluginsGrid.asSingleSelect().getValue(), this::handlePluginDelete))
                .properties(new String[] { "name", "pluginUuid", "factoryClass", "configuration"} )
                .build();
        panel.getStyle().set("margin-top", "0px");
        return panel;
    }

    private PluginsGrid constructGrid() {
        PluginsGrid grid = new PluginsGrid(plugin -> pluginPanel.applyToEntity(plugin));
        grid.addContextMenuAction("Show Actuators", actionsSupport::launchShowActuators);
        grid.addContextMenuDivider();
        grid.addContextMenuAction("Edit", pluginResponse -> actionsSupport.launchPluginEdit(pluginResponse, this::handlePluginUpdate));
        grid.addContextMenuAction("Delete", pluginResponse -> actionsSupport.launchPluginDelete(pluginResponse, this::handlePluginDelete));
        grid.setItems(pluginsAccessor.getAllPlugins());
        return grid;
    }

    private void handlePluginCreate(PluginEditDialog dialog) {
        if (actionsSupport.updatePlugin(dialog)) {
            pluginsGrid.setItems(pluginsAccessor.getAllPlugins());
            dialog.close();
        }
    }

    private void handlePluginUpdate(PluginEditDialog dialog) {
        if (actionsSupport.updatePlugin(dialog)) {
            pluginsGrid.setItems(pluginsAccessor.getAllPlugins());
            dialog.close();
        }
    }

    private void handlePluginDelete(PromptDialog<PluginResponse> dialog) {
        if (actionsSupport.deletePlugin(pluginPanel.getCurrentEntity())) {
            pluginsGrid.setItems(pluginsAccessor.getAllPlugins());
            dialog.close();
        }
    }
}
