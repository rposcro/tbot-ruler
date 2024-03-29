package com.tbot.ruler.plugins.jwavez.actuators.switchcolor;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.actuators.updatecolor.ColorMode;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SwitchColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "switch-color";

    public SwitchColorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) throws PluginException {
        SwitchColorConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchColorConfiguration.class);
        SwitchColorActuator actuator = SwitchColorActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .commandSender(pluginContext.getJwzCommandSender())
                .configuration(configuration)
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        pluginContext.getCommandRouteRegistry().registerListener(
                SwitchColorReportListener.builder()
                        .actuator(actuator)
                        .colorMode(ColorMode.valueOf(configuration.getColorMode()))
                        .sourceNodeId(configuration.getNodeId())
                        .build());
        return actuator;
    }

    @Override
    public void destroyActuator(Actuator actuator) {
        pluginContext.getCommandRouteRegistry().unregisterListener(actuator.getUuid());
    }
}
