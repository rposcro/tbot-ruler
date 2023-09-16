package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SwitchBinaryBuilder extends JWaveZActuatorBuilder {

    private final static String REFERENCE = "switch-binary";

    public SwitchBinaryBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE);
    }

    @Override
    public SwitchBinaryActuator buildActuator(ActuatorEntity actuatorEntity, JWaveZPluginContext pluginContext) {
        SwitchBinaryConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchBinaryConfiguration.class);

        return SwitchBinaryActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
    }
}
