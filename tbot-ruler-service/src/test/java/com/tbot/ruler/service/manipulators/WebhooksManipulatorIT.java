package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.model.WebhookEntity;
import com.tbot.ruler.service.lifecycle.WebhooksLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class WebhooksManipulatorIT extends BaseIT {

    @Autowired
    private WebhooksManipulator webhooksManipulator;

    @MockBean
    private WebhooksLifecycleService webhooksLifecycleService;

    @Test
    void removeWebhook_whenNoBindings_shutsDownAndDeletesWebhook() {
        WebhookEntity webhook = insertWebhook();

        webhooksManipulator.removeWebhook(webhook);

        verify(webhooksLifecycleService).shutDownWebhook(webhook.getWebhookUuid());
        assertThat(webhooksRepository.findByUuid(webhook.getWebhookUuid())).isEmpty();
    }

    @Test
    void removeWebhook_whenBindingsExist_throwsAndKeepsWebhook() {
        WebhookEntity webhook = insertWebhook();
        insertSenderBinding(webhook.getWebhookUuid());

        assertThatThrownBy(() -> webhooksManipulator.removeWebhook(webhook))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("Cannot remove webhook");

        verify(webhooksLifecycleService, never()).shutDownWebhook(webhook.getWebhookUuid());
        assertThat(webhooksRepository.findByUuid(webhook.getWebhookUuid())).isPresent();
    }
}

