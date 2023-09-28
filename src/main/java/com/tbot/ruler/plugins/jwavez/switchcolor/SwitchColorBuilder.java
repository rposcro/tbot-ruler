package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.exceptions.PluginException;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SwitchColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "switch-color";

    public SwitchColorBuilder() {
        super(REFERENCE);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, JWaveZPluginContext pluginContext) throws PluginException {
        SwitchColorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchColorConfiguration.class);
        return SwitchColorActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
    }
}
