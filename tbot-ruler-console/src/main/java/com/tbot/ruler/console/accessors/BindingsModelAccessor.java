package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.accessors.model.BindingModel;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@RouteScope
@SpringComponent
public class BindingsModelAccessor {

    @Autowired
    private BindingsAccessor bindingsAccessor;

    @Autowired
    private ActuatorsAccessor actuatorsAccessor;

    @Autowired
    private WebhooksAccessor webhooksAccessor;

    public List<BindingModel> getAllBindingsModels() {
        return toModels(bindingsAccessor.getAllBindings());
    }

    public List<BindingModel> getSenderBindingsModels(String senderUuid) {
        return toModels(bindingsAccessor.getSenderBindings(senderUuid));
    }

    public List<BindingModel> getReceiverBindingsModels(String receiverUuid) {
        return toModels(bindingsAccessor.getReceiverBindings(receiverUuid));
    }

    private List<BindingModel> toModels(List<BindingResponse> responses) {
        Map<String, ActuatorResponse> actuatorsMap = actuatorsAccessor.getActuatorsUuidMap();
        Map<String, WebhookResponse> webhooksMap = webhooksAccessor.getWebhooksUuidMap();

        return responses.stream()
                .map(response -> toModel(response, actuatorsMap, webhooksMap))
                .toList();
    }

    private BindingModel toModel(
            BindingResponse response,
            Map<String, ActuatorResponse> actuatorsMap,
            Map<String, WebhookResponse> webhooksMap) {
        String senderName;
        String senderType;

        if (actuatorsMap.containsKey(response.getSenderUuid())) {
            senderName = actuatorsMap.get(response.getSenderUuid()).getName();
            senderType = "Actuator";
        } else {
            senderName = webhooksMap.get(response.getSenderUuid()).getName();
            senderType = "Webhook";
        }

        return BindingModel.builder()
                .senderUuid(response.getSenderUuid())
                .senderName(senderName)
                .senderType(senderType)
                .receiverUuid(response.getReceiverUuid())
                .receiverName(actuatorsMap.get(response.getReceiverUuid()).getName())
                .build();
    }
}
