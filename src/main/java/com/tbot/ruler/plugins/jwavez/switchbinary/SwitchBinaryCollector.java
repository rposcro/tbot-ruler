package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.CollectorId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiConsumer;

@Getter
public class SwitchBinaryCollector extends AbstractItem<CollectorId> implements Collector {

    private final static byte SOURCE_ENDPOINT_ID = 0;

    @NonNull
    private SwitchBinaryConfiguration configuration;
    @NonNull
    private BiConsumer<NodeId, ZWaveControlledCommand> commandSender;

    @Builder
    public SwitchBinaryCollector(
            CollectorId id,
            String name,
            String description,
            SwitchBinaryConfiguration configuration,
            BiConsumer<NodeId, ZWaveControlledCommand> commandSender) {
        super(id, name, description);
        this.configuration = configuration;
        this.commandSender = commandSender;
    }

    @Override
    public void acceptMessage(Message message) {
        try {
            BooleanUpdatePayload payload = message.getPayload().ensureMessageType();
            ZWaveControlledCommand command = new SwitchBinaryCommandBuilder()
                    .buildSetCommandV1((byte) (payload.isState() ? 255 : 0));

            if (configuration.isMultiChannelOn()) {
                command = new MultiChannelCommandBuilder().encapsulateCommand(SOURCE_ENDPOINT_ID, (byte) configuration.getDestinationEndPointId(), command);
            }

            commandSender.accept(new NodeId(configuration.getNodeId()), command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }
}
