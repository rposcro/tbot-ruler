package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.NonNull;

public class SwitchMultilevelActuator extends AbstractActuator {

    private final byte switchDuration;
    private final NodeId nodeId;
    private final CommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;

    private final ActuatorState<BinaryState> actuatorState;

    private final MessagePayloadConsumer[] messageConsumers = new MessagePayloadConsumer[] {
        new MessagePayloadConsumer(BinaryClaim.class, this::consumeBinaryClaimMessage)
    };

    @Builder
    public SwitchMultilevelActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            byte switchDuration,
            @NonNull NodeId nodeId,
            @NonNull CommandSender commandSender,
            @NonNull JwzApplicationSupport applicationSupport) {
        super(uuid, name, description);
        this.switchDuration = switchDuration;
        this.nodeId = nodeId;
        this.commandSender = commandSender;
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchMultiLevelCommandBuilder();
        this.actuatorState = ActuatorState.<BinaryState>builder()
                .actuatorUuid(uuid)
                .build();
    }

    @Override
    public ActuatorState<BinaryState> getState() {
        return actuatorState;
    };

    @Override
    public void acceptMessage(Message message) {
        consumeMessage(message, this.messageConsumers);
    }

    void setState(BinaryState binaryState) {
        actuatorState.updatePayload(binaryState);
    }

    private void consumeBinaryClaimMessage(Message message) {
        BinaryClaim requestedClaim = message.getPayloadAs(BinaryClaim.class);
        BinaryState requestedState = requestedClaim.resolveState(actuatorState.getPayload());
        sendCommand(requestedState.isOn());
        setState(requestedState);
    }

    private void sendCommand(boolean state) {
        try {
            ZWaveControlledCommand command = state ? commandBuilder.v2().buildSetMaximumCommand(switchDuration)
                : commandBuilder.v2().buildSetMinimumCommand(switchDuration);
            commandSender.enqueuePrioritizedCommand(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Switch Multilevel Command sending failed!", e);
        }
    }
}
