package com.tbot.ruler.console.views.routes.actuators;

import com.tbot.ruler.console.accessors.BindingsModelAccessor;
import com.tbot.ruler.console.accessors.model.ActuatorModel;
import com.tbot.ruler.console.accessors.ActuatorsAccessor;
import com.tbot.ruler.console.accessors.PluginsAccessor;
import com.tbot.ruler.console.accessors.ThingsAccessor;
import com.tbot.ruler.console.views.AbstractActionsSupport;
import com.tbot.ruler.console.views.components.Prompt;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.tbot.ruler.console.views.routes.bindings.BindingsDialog;
import com.tbot.ruler.controller.admin.payload.ActuatorCreateRequest;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class ActuatorActionsSupport extends AbstractActionsSupport {

    @Autowired
    private ThingsAccessor thingsAccessor;

    @Autowired
    private PluginsAccessor pluginsAccessor;

    @Autowired
    private ActuatorsAccessor actuatorsAccessor;

    @Autowired
    private BindingsModelAccessor bindingsAccessor;

    public void launchShowBindings(ActuatorModel actuator) {
        BindingsDialog.builder()
                .title("Bindings of actuator " + actuator.getName())
                .inboundBindings(bindingsAccessor.getReceiverBindingsModels(actuator.getActuatorUuid()))
                .outboundBindings(bindingsAccessor.getSenderBindingsModels(actuator.getActuatorUuid()))
                .build()
                .open();
    }

    public void launchActuatorEdit(ActuatorModel actuator, EditDialogSubmittedHandler<ActuatorEditDialog> submitHandler) {
        ActuatorEditDialog.builder()
                .updateMode(true)
                .original(actuator.getActuator())
                .plugins(pluginsAccessor.getAllPlugins())
                .things(thingsAccessor.getAllThings())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchActuatorCreate(EditDialogSubmittedHandler<ActuatorEditDialog> submitHandler) {
        ActuatorEditDialog.builder()
                .updateMode(false)
                .plugins(pluginsAccessor.getAllPlugins())
                .things(thingsAccessor.getAllThings())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchActuatorDelete(ActuatorModel actuator, PromptActionHandler deleteHandler) {
        PromptDialog.builder()
                .title("Delete Actuator?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete the actuator?")
                        .addLine("Name: " + actuator.getName())
                        .addLine("UUID: " + actuator.getActuatorUuid()))
                .build()
                .open();
    }

    public boolean updateActuator(ActuatorEditDialog dialog) {
        return handlingExceptions(() -> {
            ActuatorUpdateRequest request = ActuatorUpdateRequest.builder()
                    .name(dialog.getName())
                    .description(dialog.getDescription())
                    .configuration(dialog.getConfiguration())
                    .thingUuid(dialog.getThing().getThingUuid())
                    .build();
            actuatorsAccessor.updateActuator(dialog.getOriginal().getActuatorUuid(), request);
            notifyInfo("Actuator updated");
        });
    }

    public boolean createActuator(ActuatorEditDialog dialog) {
        return handlingExceptions(() -> {
            ActuatorCreateRequest request = ActuatorCreateRequest.builder()
                    .name(dialog.getName())
                    .reference(dialog.getReference())
                    .description(dialog.getDescription())
                    .configuration(dialog.getConfiguration())
                    .pluginUuid(dialog.getPlugin().getPluginUuid())
                    .thingUuid(dialog.getThing().getThingUuid())
                    .build();
            actuatorsAccessor.createActuator(request);
            notifyInfo("New actuator created");
        });
    }

    public boolean deleteActuator(ActuatorModel actuator) {
        return handlingExceptions(() -> {
            actuatorsAccessor.deleteActuator(actuator.getActuatorUuid());
            notifyInfo("Actuator '%s' deleted", actuator.getName());
        });
    }
}
