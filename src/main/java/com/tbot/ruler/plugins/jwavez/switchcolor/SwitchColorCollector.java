package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.RGBWColor;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Collector;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SwitchColorCollector extends AbstractItem implements Collector {

    private final static String PERSISTENCE_KEY = "rgbw";

    private SwitchColorCollectorConfiguration configuration;
    private JWaveZCommandSender commandSender;

    private final SwitchColorCommandBuilder commandBuilder = new SwitchColorCommandBuilder();
    private ColorMode colorMode;

    @Builder
    public SwitchColorCollector(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull SwitchColorCollectorConfiguration configuration
    ) {
        super(id, name, description);
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.colorMode = colorMode(configuration.getColorMode());
    }

    @Override
    public void acceptMessage(Message message) {
        try {
            RGBWColor payload = message.getPayloadAs(RGBWColor.class);
            log.debug(String.format("Color switch requested: r%s g%s b%s w%s", payload.getRed(), payload.getGreen(), payload.getBlue(), payload.getWhite()));
            ZWaveControlledCommand command = buildCommand(payload);
            commandSender.accept(new NodeId(configuration.getNodeId()), command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }

    private ZWaveControlledCommand buildCommand(RGBWColor payload) {
        switch(colorMode) {
            case RGB:
                return commandBuilder.buildSetRGBCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) configuration.getSwitchDuration());
            case RGBW_COLD:
                return commandBuilder.buildSetColdRGBWCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) payload.getWhite(), (byte) configuration.getSwitchDuration());
            case RGBW_WARM:
                return commandBuilder.buildSetWarmRGBWCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) payload.getWhite(), (byte) configuration.getSwitchDuration());
            default:
                throw new MessageProcessingException("Unknown ColorMode never seen before!");
        }
    }

    private ColorMode colorMode(String colorModeParam) {
        colorModeParam = colorModeParam.trim().replace(' ', '_').toUpperCase();
        return ColorMode.valueOf(colorModeParam);
    }
}
