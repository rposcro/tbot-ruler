package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.WebhookEntity;
import com.tbot.ruler.subjects.webhook.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Scope("singleton")
public class WebhooksLifecycleService {

    @Autowired
    private WebhooksRepository webhooksRepository;

    private final Map<String, Webhook> webhooksUuidMap = new HashMap<>();

    @EventListener
    public void initialize(ApplicationStartedEvent event) {
        this.webhooksUuidMap.clear();
        webhooksRepository.findAll().forEach(this::startUpWebhook);
    }

    public Webhook getWebhookByUuid(String webhookUuid) {
        return webhooksUuidMap.get(webhookUuid);
    }

    public Webhook startUpWebhook(WebhookEntity webhookEntity) {
        if (webhooksUuidMap.containsKey(webhookEntity.getWebhookUuid())) {
            throw new LifecycleException("Webhook %s is already up", webhookEntity.getWebhookUuid());
        }

        Webhook webhook = Webhook.builder()
                .uuid(webhookEntity.getWebhookUuid())
                .owner(webhookEntity.getOwner())
                .name(webhookEntity.getName())
                .description(webhookEntity.getDescription())
                .build();
        webhooksUuidMap.put(webhook.getUuid(), webhook);
        return webhook;
    }

    public Webhook shutDownWebhook(String webhookUuid) {
        return webhooksUuidMap.remove(webhookUuid);
    }
}
