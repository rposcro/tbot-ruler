package com.tbot.ruler.plugins.deputy.binary;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.deputy.DeputyActuatorBuilder;
import com.tbot.ruler.plugins.deputy.DeputyPluginContext;
import com.tbot.ruler.subjects.actuator.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class BinaryActuatorBuilder extends DeputyActuatorBuilder {

    public BinaryActuatorBuilder() {
        super("binary-output");
    }

    public Actuator buildActuator(ActuatorEntity actuatorEntity, DeputyPluginContext deputyPluginContext) {
        BinaryActuatorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), BinaryActuatorConfiguration.class);
        BinaryActuatorChannel binaryActuatorChannel = BinaryActuatorChannel.builder()
                .pinNumber(configuration.getPinNumber())
                .deputyServiceApi(deputyPluginContext.getDeputyServiceApi())
                .build();
        return BinaryActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .binaryChannel(binaryActuatorChannel)
                .messagePublisher(deputyPluginContext.getRulerThingContext().getMessagePublisher())
                .build();
    }
}
