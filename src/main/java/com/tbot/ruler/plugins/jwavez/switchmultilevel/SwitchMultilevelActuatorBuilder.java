package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SwitchMultilevelActuatorBuilder extends JWaveZActuatorBuilder {

    private final static String REFERENCE = "switch-multilevel";

    public SwitchMultilevelActuatorBuilder() {
        super(REFERENCE);
    }

    @Override
    public SwitchMultilevelActuator buildActuator(ActuatorEntity actuatorEntity, JWaveZPluginContext pluginContext) {
        SwitchMultilevelConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SwitchMultilevelConfiguration.class);
        return SwitchMultilevelActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .switchDuration((byte) configuration.getSwitchDuration())
                .nodeId(new NodeId((byte) configuration.getNodeId()))
                .commandSender(pluginContext.getJwzCommandSender())
                .applicationSupport(pluginContext.getJwzApplicationSupport())
                .build();
    }
}
