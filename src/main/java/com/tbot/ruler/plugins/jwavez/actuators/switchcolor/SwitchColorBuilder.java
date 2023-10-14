package com.tbot.ruler.plugins.jwavez.actuators.switchcolor;

import com.rposcro.jwavez.core.commands.types.SwitchColorCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.actuators.updatecolor.ColorMode;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.exceptions.PluginException;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SwitchColorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "switch-color";

    public SwitchColorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public Actuator buildActuator(ActuatorEntity actuatorEntity) throws PluginException {
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
                SwitchColorCommandType.SWITCH_COLOR_REPORT,
                SwitchColorReportListener.builder()
                        .actuator(actuator)
                        .colorMode(ColorMode.valueOf(configuration.getColorMode()))
                        .sourceNodeId(configuration.getNodeId())
                        .build());
        return actuator;
    }
}
