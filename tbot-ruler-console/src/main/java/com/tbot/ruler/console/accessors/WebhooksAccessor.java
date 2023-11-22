package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.WebhooksClient;
import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RouteScope
@SpringComponent
public class WebhooksAccessor {

    @Autowired
    private WebhooksClient webhooksClient;

    public List<WebhookResponse> getAllWebhooks() {
        return webhooksClient.getAllWebhooks();
    }

    public Map<String, WebhookResponse> getWebhooksUuidMap() {
        return getAllWebhooks().stream()
                .collect(Collectors.toMap(WebhookResponse::getWebhookUuid, Function.identity()));
    }

    public List<String> getAvailableOwners() {
        return webhooksClient.getOwners();
    }

    public void updateWebhook(String thingUuid, WebhookUpdateRequest updateRequest) {
        webhooksClient.updateWebhook(thingUuid, updateRequest);
    }

    public void createWebhook(WebhookCreateRequest createRequest) {
        webhooksClient.createWebhook(createRequest);
    }
}
