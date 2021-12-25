package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.commands.controlled.builders.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.CollectorId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiConsumer;

@Getter
@Builder
public class SwitchMultilevelCollector implements Collector {

    @NonNull private CollectorId id;
    @NonNull private String name;
    private String description;

    private byte switchDuration;
    @NonNull private NodeId nodeId;
    @NonNull private BiConsumer<NodeId, ZWaveControlledCommand> commandConsumer;

    @Override
    public void acceptMessage(Message message) {
        try {
            BooleanUpdatePayload payload = message.getPayload().ensureMessageType();
            ZWaveControlledCommand command = new SwitchMultiLevelCommandBuilder()
                .buildSetCommand((byte) (payload.isState() ? 255 : 0), switchDuration);
            commandConsumer.accept(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }
}
