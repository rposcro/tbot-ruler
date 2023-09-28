package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.RGBWColor;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.subjects.AbstractActuator;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchColorActuator extends AbstractActuator implements Actuator {

    private final static String PERSISTENCE_KEY = "rgbw";

    private final SwitchColorConfiguration configuration;
    private final JWaveZCommandSender commandSender;
    private final SwitchColorCommandBuilder commandBuilder;
    private final ColorMode colorMode;

    private final ActuatorState<RGBWColor> actuatorState;

    @Builder
    public SwitchColorActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull SwitchColorConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
            ) {
        super(uuid, name, description);
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.colorMode = colorMode(configuration.getColorMode());
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchColorCommandBuilder();
        this.actuatorState = ActuatorState.<RGBWColor>builder()
                .actuatorUuid(uuid)
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        try {
            RGBWColor payload = message.getPayloadAs(RGBWColor.class);
            log.debug(String.format("Color switch requested: r%s g%s b%s w%s", payload.getRed(), payload.getGreen(), payload.getBlue(), payload.getWhite()));
            ZWaveControlledCommand command = buildCommand(payload);
            commandSender.enqueueCommand(NodeId.forId(configuration.getNodeId()), command);
            actuatorState.updatePayload(payload);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }

    @Override
    public ActuatorState getState() {
        return actuatorState;
    };

    private ZWaveControlledCommand buildCommand(RGBWColor payload) {
        switch(colorMode) {
            case RGB:
                return commandBuilder.v1().buildSetRGBCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) configuration.getSwitchDuration());
            case RGBW_COLD:
                return commandBuilder.v1().buildSetColdRGBWCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) payload.getWhite(), (byte) configuration.getSwitchDuration());
            case RGBW_WARM:
                return commandBuilder.v1().buildSetWarmRGBWCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) payload.getWhite(), (byte) configuration.getSwitchDuration());
            default:
                throw new MessageProcessingException("Unknown ColorMode never seen before!");
        }
    }

    private ColorMode colorMode(String colorModeParam) {
        colorModeParam = colorModeParam.trim().replace(' ', '_').toUpperCase();
        return ColorMode.valueOf(colorModeParam);
    }
}
