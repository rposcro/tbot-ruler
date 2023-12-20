package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WebhooksClient extends AbstractApiClient {

    @Autowired
    private WebhooksAdminApi webhooksAdminApi;

    public List<WebhookResponse> getAllWebhooks() {
        return executeApiFunction(() -> webhooksAdminApi.getWebhooks().execute());
    }

    public List<String> getOwners() {
        return executeApiFunction(() -> webhooksAdminApi.getOwners().execute());
    }

    public void updateWebhook(String webhookUuid, WebhookUpdateRequest updateRequest) {
        executeApiFunction(() -> webhooksAdminApi.updateWebhook(webhookUuid, updateRequest).execute());
    }

    public void createWebhook(WebhookCreateRequest createRequest) {
        executeApiFunction(() -> webhooksAdminApi.createWebhook(createRequest).execute());
    }

    public void deleteWebhook(String webhookUuid) {
        executeApiFunction(() -> webhooksAdminApi.deleteWebhook(webhookUuid).execute());
    }
}
