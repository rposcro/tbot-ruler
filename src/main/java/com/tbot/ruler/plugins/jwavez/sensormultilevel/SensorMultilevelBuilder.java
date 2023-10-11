package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.rposcro.jwavez.core.commands.types.SensorMultilevelCommandType;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class SensorMultilevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "sensor-multilevel";

    private final SensorMultilevelListener listener;

    public SensorMultilevelBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
        this.listener = new SensorMultilevelListener(pluginContext.getJwzApplicationSupport().supportedCommandParser());
        pluginContext.getJwzSerialHandler().addCommandListener(SensorMultilevelCommandType.SENSOR_MULTILEVEL_REPORT, listener);
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
            listener.registerActuator(
                    (byte) configuration.getSourceNodeId(),
                    (byte) configuration.getSourceEndPointId(),
                    actuator
            );
        } else {
            listener.registerActuator(
                    (byte) configuration.getSourceNodeId(),
                    actuator
            );
        }

        return actuator;
    }
}
