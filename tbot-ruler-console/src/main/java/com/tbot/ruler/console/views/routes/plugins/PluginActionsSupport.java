package com.tbot.ruler.console.views.routes.plugins;

import com.tbot.ruler.console.accessors.ActuatorsModelAccessor;
import com.tbot.ruler.console.accessors.PluginsAccessor;
import com.tbot.ruler.console.views.AbstractActionsSupport;
import com.tbot.ruler.console.views.components.Prompt;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.tbot.ruler.console.views.routes.actuators.ActuatorsDialog;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class PluginActionsSupport extends AbstractActionsSupport {

    @Autowired
    private ActuatorsModelAccessor actuatorsModelAccessor;

    @Autowired
    private PluginsAccessor pluginsAccessor;

    public void launchShowActuators(PluginResponse pluginResponse) {
        ActuatorsDialog.builder()
                .title("Actuators of plugin " + pluginResponse.getName())
                .actuators(actuatorsModelAccessor.getPluginActuators(pluginResponse.getPluginUuid()))
                .build()
                .open();
    }

    public void launchPluginCreate(EditDialogSubmittedHandler<PluginEditDialog> submitHandler) {
        PluginEditDialog.builder()
                .updateMode(false)
                .factories(pluginsAccessor.getAvailableFactories())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchPluginEdit(PluginResponse plugin, EditDialogSubmittedHandler<PluginEditDialog> submitHandler) {
        PluginEditDialog.builder()
                .updateMode(true)
                .factories(pluginsAccessor.getAvailableFactories())
                .submitHandler(submitHandler)
                .original(plugin)
                .build()
                .open();
    }

    public void launchPluginDelete(PluginResponse plugin, PromptActionHandler deleteHandler) {
        PromptDialog.builder()
                .title("Delete Plugin?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete the plugin?")
                        .addLine("Name: " + plugin.getName())
                        .addLine("UUID: " + plugin.getPluginUuid()))
                .build()
                .open();
    }

    public boolean createPlugin(PluginEditDialog dialog) {
        return handlingExceptions(() -> {
            PluginCreateRequest request = PluginCreateRequest.builder()
                    .name(dialog.getName())
                    .factoryClass(dialog.getFactory())
                    .configuration(dialog.getConfiguration())
                    .build();
            pluginsAccessor.createPlugin(request);
            notifyInfo("Plugin '%s' created", dialog.getName());
        });
    }

    public boolean updatePlugin(PluginEditDialog dialog) {
        return handlingExceptions(() -> {
            PluginUpdateRequest request = PluginUpdateRequest.builder()
                    .name(dialog.getName())
                    .configuration(dialog.getConfiguration())
                    .build();
            pluginsAccessor.updatePlugin(dialog.getOriginal().getPluginUuid(), request);
            notifyInfo("Plugin '%s' created", dialog.getName());
        });
    }

    public boolean deletePlugin(PluginResponse plugin) {
        return handlingExceptions(() -> {
            pluginsAccessor.deletePlugin(plugin.getPluginUuid());
            notifyInfo("Plugin '%s' deleted", plugin.getName());
        });
    }
}
