package com.tbot.ruler.plugins.jwavez.switchcolor;

import com.rposcro.jwavez.core.commands.controlled.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.RGBWUpdatePayload;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.CollectorId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BiConsumer;

@Slf4j
@Getter
@Builder
public class SwitchColorCollector implements Collector {

    @NonNull private CollectorId id;
    @NonNull private String name;
    private String description;

    @NonNull private ColorMode colorMode;
    @NonNull private byte switchDuration;
    @NonNull private NodeId nodeId;
    @NonNull private BiConsumer<NodeId, ZWaveControlledCommand> commandConsumer;

    @Builder.Default
    private final SwitchColorCommandBuilder commandBuilder = new SwitchColorCommandBuilder();

    @Override
    public void acceptMessage(Message message) {
        try {
            RGBWUpdatePayload payload = message.getPayload().ensureMessageType();
            log.debug(String.format("Color switch requested: r%s g%s b%s w%s", payload.getRed(), payload.getGreen(), payload.getBlue(), payload.getWhite()));
            ZWaveControlledCommand command = buildCommand(payload);
            commandConsumer.accept(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }

    private ZWaveControlledCommand buildCommand(RGBWUpdatePayload payload) {
        switch(colorMode) {
            case RGB:
                return commandBuilder.buildSetRGBCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), switchDuration);
            case RGBW_COLD:
                return commandBuilder.buildSetColdRGBWCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) payload.getWhite(), switchDuration);
            case RGBW_WARM:
                return commandBuilder.buildSetWarmRGBWCommand((byte) payload.getRed(), (byte) payload.getGreen(), (byte) payload.getBlue(), (byte) payload.getWhite(), switchDuration);
            default:
                throw new MessageProcessingException("Unknown ColorMode never seen before!");
        }
    }
}
