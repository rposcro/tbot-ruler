package com.tbot.ruler.plugins.jwavez.actuators.meter;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.jwavez.JWaveZActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZPluginContext;
import com.tbot.ruler.plugins.jwavez.controller.CommandListener;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class MeterActuatorBuilder extends JWaveZActuatorBuilder {

    private static final String REFERENCE = "meter";

    public MeterActuatorBuilder(JWaveZPluginContext pluginContext) {
        super(REFERENCE, pluginContext);
    }

    @Override
    public MeterActuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        MeterConfiguration configuration = parseConfiguration(actuatorEntity.getConfiguration(), MeterConfiguration.class);
        MeterActuator actuator = MeterActuator.builder()
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

    private CommandListener<? extends ZWaveSupportedCommand> buildCommandListener(MeterActuator actuator, MeterConfiguration configuration) {
        if (configuration.isMultiChannelOn()) {
            return MeterEncapsulatedCommandListener.builder()
                    .supportedCommandParser(pluginContext.getJwzApplicationSupport().supportedCommandParser())
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .sourceEndPointId(configuration.getNodeEndPointId())
                    .build();
        } else {
            return MeterCommandListener.builder()
                    .actuator(actuator)
                    .sourceNodeId(configuration.getNodeId())
                    .build();
        }
    }
}
