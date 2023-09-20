package com.tbot.ruler.plugins.jwavez.switchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class SwitchMultilevelActuator implements Actuator {

    private final String uuid;
    private final String name;
    private final String description;

    private final byte switchDuration;
    private final NodeId nodeId;
    private final JWaveZCommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;

    @Builder
    public SwitchMultilevelActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            byte switchDuration,
            @NonNull NodeId nodeId,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull JwzApplicationSupport applicationSupport) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.switchDuration = switchDuration;
        this.nodeId = nodeId;
        this.commandSender = commandSender;
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchMultiLevelCommandBuilder();
    }

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

            ZWaveControlledCommand command = onFlag ? commandBuilder.v2().buildSetMaximumCommand(switchDuration)
                    : commandBuilder.v2().buildSetMinimumCommand(switchDuration);
            commandSender.enqueueCommand(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }
}
