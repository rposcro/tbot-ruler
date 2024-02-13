package com.tbot.ruler.controller.admin;

import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.StencilsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.StencilEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.persistance.model.WebhookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepositoryAccessor {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private WebhooksRepository webhooksRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private StencilsRepository stencilsRepository;

    public ActuatorEntity findActuator(String actuatorUuid) {
        return actuatorsRepository.findByUuid(actuatorUuid)
                .orElseThrow(() -> new ServiceRequestException("Actuator %s not found!", actuatorUuid));
    }

    public WebhookEntity findWebhook(String webhookUuid) {
        return webhooksRepository.findByUuid(webhookUuid)
                .orElseThrow(() -> new ServiceRequestException("Webhook %s not found!", webhookUuid));
    }

    public ThingEntity findThing(String thingUuid) {
        return thingsRepository.findByUuid(thingUuid)
                .orElseThrow(() -> new ServiceRequestException("Thing %s not found!", thingUuid));
    }

    public ThingEntity findThing(long thingId) {
        return thingsRepository.findById(thingId)
                .orElseThrow(() -> new ServiceRequestException("Thing id %s not found!", thingId));
    }

    public PluginEntity findPlugin(String pluginUuid) {
        return pluginsRepository.findByUuid(pluginUuid)
                .orElseThrow(() -> new ServiceRequestException("Plugin %s not found!", pluginUuid));
    }

    public PluginEntity findPlugin(long pluginId) {
        return pluginsRepository.findById(pluginId)
                .orElseThrow(() -> new ServiceRequestException("Plugin id %s not found!", pluginId));
    }

    public BindingEntity findBinding(String senderUuid, String receiverUuid) {
        return bindingsRepository.find(senderUuid, receiverUuid)
                .orElseThrow(() -> new ServiceRequestException("Binding of sender %s and receiver %s not found!", senderUuid, receiverUuid));
    }

    public StencilEntity findStencil(String stencilUuid) {
        return stencilsRepository.findByUuid(stencilUuid)
                .orElseThrow(() -> new ServiceRequestException("Stencil %s not found!", stencilUuid));
    }
}
