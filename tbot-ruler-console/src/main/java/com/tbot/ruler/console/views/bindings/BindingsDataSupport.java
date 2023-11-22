package com.tbot.ruler.console.views.bindings;

import com.tbot.ruler.console.clients.ActuatorsClient;
import com.tbot.ruler.console.clients.BindingsClient;
import com.tbot.ruler.console.clients.WebhooksClient;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringComponent
public class BindingsDataSupport {

    @Autowired
    private BindingsClient bindingsClient;

    @Autowired
    private ActuatorsClient actuatorsClient;

    @Autowired
    private WebhooksClient webhooksClient;

    public List<BindingModel> fetchAllBindings() {
        Map<String, ActuatorResponse> actuatorsMap = actuatorsMap();
        Map<String, WebhookResponse> webhooksMap = webhooksMap();

        return bindingsClient.getAllBindings().stream()
                .map(response -> toModel(response, actuatorsMap, webhooksMap))
                .toList();
    }

    public void createBinding(String senderUuid, String receiverUuid) {
        bindingsClient.createBinding(senderUuid, receiverUuid);
    }

    public void deleteBinding(String senderUuid, String receiverUuid) {
        bindingsClient.deleteBinding(senderUuid, receiverUuid);
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

    private Map<String, ActuatorResponse> actuatorsMap() {
        return actuatorsClient.getAllActuators().stream()
                .collect(Collectors.toMap(ActuatorResponse::getActuatorUuid, Function.identity()));
    }

    private Map<String, WebhookResponse> webhooksMap() {
        return webhooksClient.getAllWebhooks().stream()
                .collect(Collectors.toMap(WebhookResponse::getWebhookUuid, Function.identity()));
    }
}
