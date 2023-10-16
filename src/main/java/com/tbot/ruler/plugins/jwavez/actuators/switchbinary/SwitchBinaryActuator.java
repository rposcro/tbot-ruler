package com.tbot.ruler.plugins.jwavez.actuators.switchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SwitchBinaryActuator extends AbstractSubject implements Actuator {

    private final static byte SOURCE_ENDPOINT_ID = 0;

    private final SwitchBinaryConfiguration configuration;
    private final CommandSender commandSender;
    private final SwitchBinaryCommandBuilder switchBinaryCommandBuilder;
    private final MultiChannelCommandBuilder multiChannelCommandBuilder;

    private final ActuatorState<OnOffState> state;

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
        this.state = ActuatorState.<OnOffState>builder()
                .actuatorUuid(uuid)
                .build();
    }

    @Override
    public void acceptMessage(Message message) {
        try {
            OnOffState payload = message.getPayloadAs(OnOffState.class);
            setState(payload);

            ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) (payload.isOn() ? 255 : 0));
            if (configuration.isMultiChannelOn()) {
                command = multiChannelCommandBuilder.v3().encapsulateCommand(SOURCE_ENDPOINT_ID, (byte) configuration.getNodeEndPointId(), command);
            }
            commandSender.enqueueCommand(NodeId.forId(configuration.getNodeId()), command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    public void setState(OnOffState onOffState) {
        state.updatePayload(onOffState);
    }
}
