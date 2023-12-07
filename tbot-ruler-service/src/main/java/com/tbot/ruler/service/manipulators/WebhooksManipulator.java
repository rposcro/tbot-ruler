package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.WebhookEntity;
import com.tbot.ruler.service.lifecycle.WebhooksLifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebhooksManipulator {

    @Autowired
    private WebhooksRepository webhooksRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private WebhooksLifecycleService webhooksLifecycleService;

    public void removeWebhook(WebhookEntity webhookEntity) {
        assertConsistency(webhookEntity.getWebhookUuid());
        webhooksLifecycleService.shutDownWebhook(webhookEntity.getWebhookUuid());
        webhooksRepository.delete(webhookEntity);
    }

    private void assertConsistency(String webhookUuid) {
        if (bindingsRepository.bindingWithUuidExists(webhookUuid)) {
            throw new LifecycleException("Cannot remove webhook %s, binding(s) exist(s)!", webhookUuid);
        }
    }
}
