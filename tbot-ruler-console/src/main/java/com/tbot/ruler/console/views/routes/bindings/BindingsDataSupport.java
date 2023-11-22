package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.ActuatorsAccessor;
import com.tbot.ruler.console.accessors.BindingsAccessor;
import com.tbot.ruler.console.accessors.WebhooksAccessor;
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
public class BindingsDataSupport {

    @Autowired
    private BindingsAccessor bindingsAccessor;

    @Autowired
    private ActuatorsAccessor actuatorsAccessor;

    @Autowired
    private WebhooksAccessor webhooksAccessor;

    public List<BindingModel> getAllBindingsModels() {
        Map<String, ActuatorResponse> actuatorsMap = actuatorsAccessor.getActuatorsUuidMap();
        Map<String, WebhookResponse> webhooksMap = webhooksAccessor.getWebhooksUuidMap();

        return bindingsAccessor.getAllBindings().stream()
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
