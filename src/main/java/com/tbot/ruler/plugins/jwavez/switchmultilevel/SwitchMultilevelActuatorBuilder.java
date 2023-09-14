package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.plugins.jwavez.ActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandListener;
import com.tbot.ruler.plugins.jwavez.JWaveZThingContext;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;

public class SwitchMultilevelActuatorBuilder implements ActuatorBuilder {

    private static final String SWITCH_PARAM_NODE_ID = "nodeId";
    private static final String SWITCH_PARAM_SWITCH_DURATION = "switchDuration";

    private final static String REFERENCE = "switch-multilevel";

    private final JWaveZThingContext thingContext;

    public SwitchMultilevelActuatorBuilder(JWaveZThingContext thingContext) {
        this.thingContext = thingContext;
    }

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public SwitchMultilevelActuator buildActuator(ActuatorDTO actuatorDTO) {
        return SwitchMultilevelActuator.builder()
                .id(actuatorDTO.getId())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getDescription())
                .switchDuration((byte) actuatorDTO.getIntParameter(SWITCH_PARAM_SWITCH_DURATION, 0))
                .nodeId(new NodeId((byte) actuatorDTO.getIntParameter(SWITCH_PARAM_NODE_ID)))
                .commandSender(thingContext.getJwzCommandSender())
                .applicationSupport(thingContext.getJwzApplicationSupport())
                .build();
    }

    @Override
    public CommandType getSupportedCommandType() {
        return null;
    }

    @Override
    public JWaveZCommandListener<? extends ZWaveSupportedCommand> getSupportedCommandHandler() {
        return null;
    }
}
