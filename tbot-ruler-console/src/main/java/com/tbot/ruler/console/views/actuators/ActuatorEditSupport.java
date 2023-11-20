package com.tbot.ruler.console.views.actuators;

import com.tbot.ruler.console.clients.ActuatorsClient;
import com.tbot.ruler.console.clients.PluginsClient;
import com.tbot.ruler.console.clients.ThingsClient;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.PopupNotifier;
import com.tbot.ruler.controller.admin.payload.ActuatorCreateRequest;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.tbot.ruler.console.utils.StreamUtils.toMap;

@SpringComponent
public class ActuatorEditSupport {

    @Autowired
    private ActuatorsClient actuatorsClient;

    @Autowired
    private ThingsClient thingsClient;

    @Autowired
    private PluginsClient pluginsClient;

    @Autowired
    private PopupNotifier popupNotifier;

    public List<ActuatorResponse> fetchAllActuators() {
        return actuatorsClient.getAllActuators();
    }

    public List<ActuatorModel> fetchAllActuatorsModels() {
        Map<String, PluginResponse> pluginsMap = toMap(pluginsClient.getAllPlugins(), PluginResponse::getPluginUuid);
        Map<String, ThingResponse> thingsMap = toMap(thingsClient.getAllThings(), ThingResponse::getThingUuid);

        return actuatorsClient.getAllActuators().stream()
                .map(actuatorResponse -> ActuatorModel.builder()
                        .actuator(actuatorResponse)
                        .plugin(pluginsMap.get(actuatorResponse.getPluginUuid()))
                        .thing(thingsMap.get(actuatorResponse.getThingUuid()))
                        .build())
                .toList();
    }

    public void launchActuatorEdit(ActuatorModel actuator, Consumer<ActuatorEditDialog> submitHandler) {
        ActuatorEditDialog.builder()
                .updateMode(true)
                .original(actuator.getActuator())
                .plugins(pluginsClient.getAllPlugins())
                .things(thingsClient.getAllThings())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchActuatorCreate(Consumer<ActuatorEditDialog> submitHandler) {
        ActuatorEditDialog.builder()
                .updateMode(false)
                .plugins(pluginsClient.getAllPlugins())
                .things(thingsClient.getAllThings())
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public boolean updateActuator(ActuatorEditDialog dialog) {
        try {
            ActuatorUpdateRequest request = ActuatorUpdateRequest.builder()
                    .name(dialog.getName())
                    .description(dialog.getDescription())
                    .configuration(dialog.getConfiguration())
                    .build();
            actuatorsClient.updateActuator(dialog.getOriginal().getActuatorUuid(), request);
            popupNotifier.notifyInfo("Actuator updated");
            return true;
        } catch(ClientCommunicationException e) {
            popupNotifier.notifyError("Failed to request service update!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
        }

        return false;
    }

    public boolean createActuator(ActuatorEditDialog dialog) {
        try {
            ActuatorCreateRequest request = ActuatorCreateRequest.builder()
                    .name(dialog.getName())
                    .reference(dialog.getReference())
                    .description(dialog.getDescription())
                    .configuration(dialog.getConfiguration())
                    .pluginUuid(dialog.getPlugin().getPluginUuid())
                    .thingUuid(dialog.getThing().getThingUuid())
                    .build();
            actuatorsClient.createActuator(request);
            popupNotifier.notifyInfo("New actuator created");
            return true;
        } catch(ClientCommunicationException e) {
            popupNotifier.notifyError("Failed to request service create!");
        } catch(Exception e) {
            popupNotifier.notifyError("Something's wrong!");
        }

        return false;
    }
}
