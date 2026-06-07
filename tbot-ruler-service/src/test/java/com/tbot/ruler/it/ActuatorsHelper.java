package com.tbot.ruler.it;

import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActuatorsHelper {

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    public ActuatorEntity insertActuator(long pluginId, long thingId) {
        ActuatorEntity actuatorEntity = newActuatorEntity(pluginId, thingId);
        return actuatorsRepository.save(actuatorEntity);
    }

    public ActuatorEntity newActuatorEntity(long pluginId, long thingId) {
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
}
