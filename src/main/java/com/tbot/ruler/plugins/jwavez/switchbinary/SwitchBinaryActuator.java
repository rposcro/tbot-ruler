package com.tbot.ruler.plugins.jwavez.switchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Collector;
import lombok.Builder;
import lombok.Getter;


@Getter
public class SwitchBinaryActuator extends AbstractItem implements Actuator {

    private final static byte SOURCE_ENDPOINT_ID = 0;

    private final SwitchBinaryConfiguration configuration;
    private final JWaveZCommandSender commandSender;
    private final SwitchBinaryCommandBuilder switchBinaryCommandBuilder;
    private final MultiChannelCommandBuilder multiChannelCommandBuilder;

    @Builder
    public SwitchBinaryActuator(
            String id,
            String name,
            String description,
            SwitchBinaryConfiguration configuration,
            JWaveZCommandSender commandSender,
            JwzApplicationSupport applicationSupport) {
        super(id, name, description);
        this.configuration = configuration;
        this.commandSender = commandSender;
        this.switchBinaryCommandBuilder = applicationSupport.controlledCommandFactory().switchBinaryCommandBuilder();
        this.multiChannelCommandBuilder = applicationSupport.controlledCommandFactory().multiChannelCommandBuilder();
    }

    @Override
    public void acceptMessage(Message message) {
        try {
            OnOffState payload = message.getPayloadAs(OnOffState.class);
            ZWaveControlledCommand command = switchBinaryCommandBuilder.v1().buildSetCommand((byte) (payload.isOn() ? 255 : 0));

            if (configuration.isMultiChannelOn()) {
                command = multiChannelCommandBuilder.v3().encapsulateCommand(SOURCE_ENDPOINT_ID, (byte) configuration.getDestinationEndPointId(), command);
            }

            commandSender.enqueueCommand(NodeId.forId(configuration.getNodeId()), command);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }
}
