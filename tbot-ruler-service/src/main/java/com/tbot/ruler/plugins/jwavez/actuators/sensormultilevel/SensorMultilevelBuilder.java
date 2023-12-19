package com.tbot.ruler.plugins.jwavez.actuators.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class SensorMultilevelBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "sensor-multilevel";

    public SensorMultilevelBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public SensorMultilevelActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        SensorMultilevelConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), SensorMultilevelConfiguration.class);
        SensorMultilevelActuator actuator = SensorMultilevelActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .messagePublisher(rulerThingContext.getMessagePublisher())
                .build();
        CommandListener<? extends ZWaveSupportedCommand> listener = buildCommandListener(actuator, configuration);
        pluginContext.getCommandRouteRegistry().registerListener(listener);
        return actuator;
    }

    @Override
    public void destroyActuator(Actuator actuator) {
        pluginContext.getCommandRouteRegistry().unregisterListener(actuator.getUuid());
    }

    private CommandListener<? extends ZWaveSupportedCommand> buildCommandListener(SensorMultilevelActuator actuator, SensorMultilevelConfiguration configuration) {
        if (configuration.isMultiChannelOn()) {
            return SensorMultilevelEncapsulatedCommandListener.builder()
                    .supportedCommandParser(pluginContext.getJwzApplicationSupport().supportedCommandParser())
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .sourceEndPointId(configuration.getNodeEndPointId())
                    .build();
        } else {
            return SensorMultilevelCommandListener.builder()
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .build();
        }
    }
}
