package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
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

    private final ActuatorState<OnOffState> actuatorState;

    private final MessagePayloadConsumer[] messageConsumers = new MessagePayloadConsumer[] {
        new MessagePayloadConsumer(OnOffState.class, this::consumeOnOffMessage),
        new MessagePayloadConsumer(BinaryStateClaim.class, this::consumeBinaryStateClaimMessage)
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
        this.actuatorState = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .build();
    }

    @Override
    public ActuatorState<OnOffState> getState() {
        return actuatorState;
    };

    @Override
    public void acceptMessage(Message message) {
        consumeMessage(message, this.messageConsumers);
    }

    void setState(OnOffState onOffState) {
        actuatorState.updatePayload(onOffState);
    }

    void acceptCommand(SwitchMultilevelReport report) {
        this.actuatorState.updatePayload(OnOffState.of(report.getCurrentValue() != 0));
    }

    private void consumeOnOffMessage(Message message) {
        OnOffState payload = message.getPayloadAs(OnOffState.class);
        sendCommand(payload.isOn());
        setState(payload);
    }

    private void consumeBinaryStateClaimMessage(Message message) {
        BinaryStateClaim claim = message.getPayloadAs(BinaryStateClaim.class);
        boolean desiredState;
        if (claim.isToggle()) {
            desiredState = actuatorState.getPayload() == null || !actuatorState.getPayload().isOn();
        } else {
            desiredState = claim.isSetOn();
        }
        sendCommand(desiredState);
        setState(desiredState ? OnOffState.STATE_ON : OnOffState.STATE_OFF);
    }

    private void sendCommand(boolean state) {
        try {
            ZWaveControlledCommand command = state ? commandBuilder.v2().buildSetMaximumCommand(switchDuration)
                : commandBuilder.v2().buildSetMinimumCommand(switchDuration);
            commandSender.enqueueCommand(nodeId, command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Switch Multilevel Command sending failed!", e);
        }
    }
}
