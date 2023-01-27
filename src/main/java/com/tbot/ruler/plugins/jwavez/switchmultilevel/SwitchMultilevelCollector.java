package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.commands.controlled.builders.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.model.BinaryStateClaim;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
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
    @NonNull private JWaveZCommandSender commandSender;

    @Builder.Default
    private SwitchMultiLevelCommandBuilder commandBuilder = new SwitchMultiLevelCommandBuilder();

    @Override
    public void acceptMessage(Message message) {
        try {
            boolean onFlag;

            if (message.isPayloadAs(BinaryStateClaim.class)) {
                BinaryStateClaim claim = message.getPayloadAs(BinaryStateClaim.class);
                if (claim.isToggle()) {
                    throw new MessageUnsupportedException("Cannot support toggle claim!");
                }
                onFlag = claim.isSetOn();
            } else if (message.isPayloadAs(OnOffState.class)) {
                OnOffState payload = message.getPayloadAs(OnOffState.class);
                onFlag = payload.isOn();
            } else {
                throw new MessageUnsupportedException("Message of type " + message.getPayload().getClass() + " is not supported here!");
            }

            ZWaveControlledCommand command = onFlag ? commandBuilder.buildSetMaximumCommand(switchDuration)
                    :commandBuilder.buildSetMinimumCommand(switchDuration);
            commandSender.enqueueCommand(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }
}
