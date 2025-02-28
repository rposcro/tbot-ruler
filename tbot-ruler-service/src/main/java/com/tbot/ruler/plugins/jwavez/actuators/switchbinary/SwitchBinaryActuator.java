package com.tbot.ruler.plugins.jwavez.actuators.switchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
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
import lombok.Getter;

@Getter
public class SwitchBinaryActuator extends AbstractActuator {

    private final static byte SOURCE_ENDPOINT_ID = 0;

    private final SwitchBinaryConfiguration configuration;
    private final CommandSender commandSender;
    private final SwitchBinaryCommandBuilder switchBinaryCommandBuilder;
    private final MultiChannelCommandBuilder multiChannelCommandBuilder;

    private final ActuatorState<BinaryState> state;

    private final MessagePayloadConsumer[] messageConsumers = new MessagePayloadConsumer[] {
        new MessagePayloadConsumer(BinaryState.class, this::consumeOnOffMessage),
        new MessagePayloadConsumer(BinaryClaim.class, this::consumeBinaryStateClaimMessage)
    };

    @Builder
    public SwitchBinaryActuator(
            String uuid,
            String name,
            String description,
            SwitchBinaryConfiguration configuration,
            CommandSender commandSender,
            JwzApplicationSupport applicationSupport) {
        super(uuid, name, description);
        this.configuration = configuration;
        this.commandSender = commandSender;
        this.switchBinaryCommandBuilder = applicationSupport.controlledCommandFactory().switchBinaryCommandBuilder();
        this.multiChannelCommandBuilder = applicationSupport.controlledCommandFactory().multiChannelCommandBuilder();
        this.state = ActuatorState.<BinaryState>builder()
                .actuatorUuid(uuid)
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        consumeMessage(message, this.messageConsumers);
    }

    void setState(BinaryState binaryState) {
        state.updatePayload(binaryState);
    }

    private void consumeOnOffMessage(Message message) {
        BinaryState payload = message.getPayloadAs(BinaryState.class);
        sendCommand(payload.isOn());
        setState(payload);
    }

    private void consumeBinaryStateClaimMessage(Message message) {
        BinaryClaim claim = message.getPayloadAs(BinaryClaim.class);
        boolean desiredState;
        if (claim.isToggle()) {
            desiredState = state.getPayload() == null || !state.getPayload().isOn();
        } else {
            desiredState = claim.isSetOn();
        }
        sendCommand(desiredState);
        setState(desiredState ? BinaryState.ON : BinaryState.OFF);
    }

    private void sendCommand(boolean state) {
        try {
            ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) (state ? 255 : 0));
            if (configuration.isMultiChannelOn()) {
                command = multiChannelCommandBuilder.v3().encapsulateCommand(SOURCE_ENDPOINT_ID, (byte) configuration.getNodeEndPointId(), command);
            }
            commandSender.enqueueCommand(NodeId.forId(configuration.getNodeId()), command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Switch Binary Command sending failed!", e);
        }
    }
}
