package com.tbot.ruler;

import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.persistance.model.WebhookEntity;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest(properties = "spring.config.name=tbot-ruler-service")
@AutoConfigureMockMvc
@ActiveProfiles({"it"})
public class BaseIT {

    @Autowired
    protected PluginsRepository pluginsRepository;

    @Autowired
    protected ActuatorsRepository actuatorsRepository;

    @Autowired
    protected ThingsRepository thingsRepository;

    @SpyBean
    protected BindingsRepository bindingsRepository;

    @Autowired
    protected WebhooksRepository webhooksRepository;

    @AfterEach
    void tearDown() {
        actuatorsRepository.deleteAll();
        pluginsRepository.deleteAll();
        thingsRepository.deleteAll();
        bindingsRepository.deleteAll();
    }

    protected PluginEntity insertPlugin() {
        PluginEntity pluginEntity = newPluginEntity();
        return pluginsRepository.save(pluginEntity);
    }

    protected ThingEntity insertThing() {
        ThingEntity thingEntity = newThingEntity();
        return thingsRepository.save(thingEntity);
    }

    protected ActuatorEntity insertActuator(long pluginId, long thingId) {
        ActuatorEntity actuatorEntity = newActuatorEntity(pluginId, thingId);
        return actuatorsRepository.save(actuatorEntity);
    }

    protected BindingEntity insertBinding(String senderUuid, String receiverUuid) {
        BindingEntity bindingEntity = BindingEntity.builder()
            .senderUuid(senderUuid)
            .receiverUuid(receiverUuid)
            .build();
        bindingsRepository.insert(bindingEntity);
        return bindingEntity;
    }

    protected BindingEntity insertSenderBinding(String senderUuid) {
        return insertBinding(senderUuid, "rcv-" + UUID.randomUUID());
    }

    protected WebhookEntity insertWebhook() {
        WebhookEntity webhookEntity = newWebhookEntity();
        return webhooksRepository.save(webhookEntity);
    }

    protected PluginEntity newPluginEntity() {
        return PluginEntity.builder()
            .pluginUuid("plgn-" + UUID.randomUUID())
            .factoryClass("com.tbot.ruler.plugins.TestPlugin")
            .name("Plugin Name")
            .configuration(new TextNode("plugin-config"))
            .build();
    }

    protected ThingEntity newThingEntity() {
        return ThingEntity.builder()
            .thingUuid("thng-" + UUID.randomUUID())
            .name("Thing Name")
            .build();
    }

    protected ActuatorEntity newActuatorEntity(long pluginId, long thingId) {
        return ActuatorEntity.builder()
            .actuatorUuid("actr-" + UUID.randomUUID())
            .thingId(thingId)
            .pluginId(pluginId)
            .reference("actuator-reference")
            .name("Actuator Name")
            .description("Actuator Description")
            .configuration(new TextNode("actuator-config"))
            .build();
    }

    protected WebhookEntity newWebhookEntity() {
        return WebhookEntity.builder()
            .webhookUuid("whk-" + UUID.randomUUID())
            .owner("owner")
            .name("Webhook Name")
            .description("Webhook Description")
            .build();
    }
}
