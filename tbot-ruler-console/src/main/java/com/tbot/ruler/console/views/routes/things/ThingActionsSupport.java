package com.tbot.ruler.console.views.routes.things;

import com.tbot.ruler.console.accessors.ActuatorsModelAccessor;
import com.tbot.ruler.console.accessors.ThingsAccessor;
import com.tbot.ruler.console.views.AbstractActionsSupport;
import com.tbot.ruler.console.views.components.Prompt;
import com.tbot.ruler.console.views.components.PromptDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.components.handlers.PromptActionHandler;
import com.tbot.ruler.console.views.routes.actuators.ActuatorsDialog;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import com.tbot.ruler.controller.admin.payload.ThingCreateRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.ThingUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class ThingActionsSupport extends AbstractActionsSupport {

    @Autowired
    private ThingsAccessor thingsAccessor;

    @Autowired
    private ActuatorsModelAccessor actuatorsModelAccessor;

    public void launchShowActuators(ThingResponse thing) {
        ActuatorsDialog.builder()
                .title("Actuators of thing " + thing.getName())
                .actuators(actuatorsModelAccessor.getThingActuators(thing.getThingUuid()))
                .build()
                .open();
    }

    public void launchThingCreate(EditDialogSubmittedHandler<ThingEditDialog> submitHandler) {
        ThingEditDialog.builder()
                .updateMode(false)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchThingEdit(ThingResponse thing, EditDialogSubmittedHandler<ThingEditDialog> submitHandler) {
        ThingEditDialog.builder()
                .updateMode(true)
                .submitHandler(submitHandler)
                .original(thing)
                .build()
                .open();
    }

    public void launchThingDelete(ThingResponse thing, PromptActionHandler deleteHandler) {
        PromptDialog.builder()
                .title("Delete Thing?")
                .action("Delete", deleteHandler)
                .action("Cancel", PromptDialog::close)
                .prompt(new Prompt()
                        .addLine("Are you sure to delete the thing?")
                        .addLine("Name: " + thing.getName())
                        .addLine("UUID: " + thing.getThingUuid()))
                .build()
                .open();
    }

    public boolean createThing(ThingEditDialog dialog) {
        return handlingExceptions(() -> {
            ThingCreateRequest request = ThingCreateRequest.builder()
                    .name(dialog.getName())
                    .description(dialog.getDescription())
                    .configuration(dialog.getConfiguration())
                    .build();
            thingsAccessor.createThing(request);
            notifyInfo("Thing created");
        });
    }

    public boolean updateThing(ThingEditDialog dialog) {
        return handlingExceptions(() -> {
            ThingUpdateRequest request = ThingUpdateRequest.builder()
                    .name(dialog.getName())
                    .description(dialog.getDescription())
                    .configuration(dialog.getConfiguration())
                    .build();
            thingsAccessor.updateThing(dialog.getOriginal().getThingUuid(), request);
            notifyInfo("Thing updated");
        });
    }

    public boolean deleteThing(ThingResponse thing) {
        return handlingExceptions(() -> {
            thingsAccessor.deleteThing(thing.getThingUuid());
            notifyInfo("Thing '%s' deleted", thing.getName());
        });
    }
}
