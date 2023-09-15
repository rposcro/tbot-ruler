package com.tbot.ruler.plugins.jwavez.basicset;

import com.rposcro.jwavez.core.commands.types.BasicCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.things.Actuator;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class BasicSetBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "basic-set";

    private final JWaveZPluginContext pluginContext;

    public BasicSetBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, BasicCommandType.BASIC_SET, new BasicSetCommandListener(pluginContext.getJwzApplicationSupport().supportedCommandParser()));
        this.pluginContext = pluginContext;
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) {
        BasicSetConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), BasicSetConfiguration.class);
        BasicSetActuator emitter = BasicSetActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .configuration(configuration)
                .messagePublisher(pluginContext.getMessagePublisher())
                .build();
        ((BasicSetCommandListener) getSupportedCommandHandler()).registerEmitter(emitter);
        return emitter;
    }
}
