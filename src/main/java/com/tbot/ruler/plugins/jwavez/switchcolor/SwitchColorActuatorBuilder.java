package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorSet;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.handlers.SupportedCommandHandler;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZAgent;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

import static com.rposcro.jwavez.core.commands.types.SwitchColorCommandType.SWITCH_COLOR_SET;

public class SwitchColorActuatorBuilder implements ActuatorBuilder {

    private static final String REFERENCE = "switch-color";
    private static final String SWITCH_PARAM_NODE_ID = "nodeId";
    private static final String SWITCH_PARAM_COLOR_MODE = "colorMode";
    private static final String SWITCH_PARAM_SWITCH_DURATION = "switchduration";

    @Override
    public CommandType getSupportedCommandType() {
        return SWITCH_COLOR_SET;
    }

    @Override
    public SupportedCommandHandler<SwitchColorSet> getSupportedCommandHandler() {
        return (SupportedCommandHandler<SwitchColorSet>) null;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SwitchColorActuator buildActuator(JWaveZAgent agent, ThingBuilderContext context, ActuatorDTO actuatorDTO) {
        return SwitchColorActuator.builder()
            .id(actuatorDTO.getId())
            .name(actuatorDTO.getName())
            .description(actuatorDTO.getDescription())
            .switchDuration((byte) actuatorDTO.getIntParameter(SWITCH_PARAM_SWITCH_DURATION, 0))
            .nodeId(new NodeId((byte) actuatorDTO.getIntParameter(SWITCH_PARAM_NODE_ID)))
            .colorMode(colorMode(actuatorDTO.getStringParameter(SWITCH_PARAM_COLOR_MODE)))
            .commandConsumer(agent.commandSender())
            .messagePublisher(context.getMessagePublisher())
            .build();
    }

    private ColorMode colorMode(String colorModeParam) {
        colorModeParam = colorModeParam.trim().replace(' ', '_').toUpperCase();
        return ColorMode.valueOf(colorModeParam);
    }
}
