package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.commands.controlled.builders.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.things.Collector;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.BiConsumer;

@Getter
@Builder
public class SwitchMultilevelCollector implements Collector {

    @NonNull private String id;
    @NonNull private String name;
    private String description;

    private byte switchDuration;
    @NonNull private NodeId nodeId;
    @NonNull private BiConsumer<NodeId, ZWaveControlledCommand> commandSender;

    @Override
    public void acceptMessage(Message message) {
        try {
            OnOffState payload = message.getPayloadAs(OnOffState.class);
            ZWaveControlledCommand command = new SwitchMultiLevelCommandBuilder()
                .buildSetCommand((byte) (payload.isOn() ? 255 : 0), switchDuration);
            commandSender.accept(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }
}
