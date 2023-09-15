package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SensorMultilevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "sensor-multilevel";

    private final JWaveZPluginContext pluginContext;

    public SensorMultilevelBuilder(JWaveZPluginContext pluginContext) {
        super(
                REFERENCE,
                SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT,
                new SensorMultilevelListener(
                        pluginContext.getJwzApplicationSupport().supportedCommandParser())
        );
        this.pluginContext = pluginContext;
    }

    @Override
    public SensorMultilevelActuator buildActuator(ActuatorEntity actuatorEntity) {
        SensorMultilevelConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SensorMultilevelConfiguration.class);
        SensorMultilevelActuator actuator = SensorMultilevelActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .messagePublisher(pluginContext.getMessagePublisher())
                .build();

        if (configuration.isMultiChannelOn()) {
            ((SensorMultilevelListener) getSupportedCommandHandler()).registerEmitter(
                    (byte) configuration.getSourceNodeId(),
                    (byte) configuration.getSourceEndPointId(),
                    actuator
            );
        } else {
            ((SensorMultilevelListener) getSupportedCommandHandler()).registerEmitter(
                    (byte) configuration.getSourceNodeId(),
                    actuator
            );
        }

        return actuator;
    }
}
