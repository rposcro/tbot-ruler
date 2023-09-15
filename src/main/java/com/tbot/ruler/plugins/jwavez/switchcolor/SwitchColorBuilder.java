package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.exceptions.PluginException;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SwitchColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "switch-color";

    private final JWaveZPluginContext pluginContext;

    public SwitchColorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE);
        this.pluginContext = pluginContext;
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) throws PluginException {
        SwitchColorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchColorConfiguration.class);
        return SwitchColorActuator.builder()
                .id(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
    }
}
