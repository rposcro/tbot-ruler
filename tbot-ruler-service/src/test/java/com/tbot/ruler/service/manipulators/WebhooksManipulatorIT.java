package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.it.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.it.BindingsHelper;
import com.tbot.ruler.it.WebhooksHelper;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.WebhookEntity;
import com.tbot.ruler.service.lifecycle.WebhooksLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebhooksManipulatorIT extends BaseIT {

    @Autowired
    private WebhooksManipulator webhooksManipulator;

    @Autowired
    private WebhooksLifecycleService webhooksLifecycleService;

    @Autowired
    private BindingsHelper bindingsHelper;

    @Autowired
    private WebhooksHelper webhooksHelper;

    @Autowired
    private WebhooksRepository webhooksRepository;

    @Test
    void removeWebhook_whenNoBindings_shutsDownAndDeletesWebhook() {
        WebhookEntity webhookEntity = webhooksHelper.newWebhookEntity();
        String webhookUuid = webhookEntity.getWebhookUuid();
        webhooksManipulator.createWebhook(webhookEntity);

        assertThat(webhooksLifecycleService.getWebhookByUuid(webhookUuid)).isNotNull();
        assertThat(webhooksRepository.findByUuid(webhookUuid)).isPresent();

        webhooksManipulator.removeWebhook(webhookEntity);

        assertThat(webhooksLifecycleService.getWebhookByUuid(webhookUuid)).isNull();
        assertThat(webhooksRepository.findByUuid(webhookUuid)).isEmpty();
    }

    @Test
    void removeWebhook_whenBindingsExist_throwsAndKeepsWebhook() {
        WebhookEntity webhookEntity = webhooksHelper.newWebhookEntity();
        String webhookUuid = webhookEntity.getWebhookUuid();
        webhooksManipulator.createWebhook(webhookEntity);
        bindingsHelper.insertSenderBinding(webhookUuid);

        assertThatThrownBy(() -> webhooksManipulator.removeWebhook(webhookEntity))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("Cannot remove webhook");

        assertThat(webhooksLifecycleService.getWebhookByUuid(webhookUuid)).isNotNull();
        assertThat(webhooksRepository.findByUuid(webhookUuid)).isPresent();
    }
}

