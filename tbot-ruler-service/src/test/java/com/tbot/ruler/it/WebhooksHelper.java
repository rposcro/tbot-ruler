package com.tbot.ruler.it;

import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.WebhookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebhooksHelper {

    @Autowired
    private WebhooksRepository webhooksRepository;

    public WebhookEntity insertWebhook() {
        WebhookEntity webhookEntity = newWebhookEntity();
        return webhooksRepository.save(webhookEntity);
    }

    public WebhookEntity newWebhookEntity() {
        return WebhookEntity.builder()
                .webhookUuid("whk-" + UUID.randomUUID())
                .owner("owner")
                .name("Webhook Name")
                .description("Webhook Description")
                .build();
    }
}
