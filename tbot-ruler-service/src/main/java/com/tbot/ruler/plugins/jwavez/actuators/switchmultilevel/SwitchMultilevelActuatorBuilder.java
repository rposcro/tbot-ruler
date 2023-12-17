package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.rposcro.jwavez.core.commands.types.SwitchMultiLevelCommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SwitchMultilevelActuatorBuilder extends JWaveZActuatorBuilder {

    private final static String REFERENCE = "switch-multilevel";

    public SwitchMultilevelActuatorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public SwitchMultilevelActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        SwitchMultilevelConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchMultilevelConfiguration.class);
        SwitchMultilevelActuator actuator = SwitchMultilevelActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .switchDuration((byte) configuration.getSwitchDuration())
                .nodeId(new NodeId((byte) configuration.getNodeId()))
                .commandSender(pluginContext.getJwzCommandSender())
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
        pluginContext.getCommandRouteRegistry().registerListener(
                SwitchMultiLevelCommandType.SWITCH_MULTILEVEL_REPORT,
                SwitchMultiLevelReportListener.builder()
                        .actuator(actuator)
                        .sourceNodeId(configuration.getNodeId())
                        .build()
        );
        return actuator;
    }
}
