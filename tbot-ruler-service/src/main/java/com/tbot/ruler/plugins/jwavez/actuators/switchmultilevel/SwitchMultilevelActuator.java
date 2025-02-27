package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.exceptions.JWaveZException;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.NonNull;

import static com.tbot.ruler.plugins.StatesUtil.determineOnOffState;

public class SwitchMultilevelActuator extends AbstractActuator {

    private final byte switchDuration;
    private final NodeId nodeId;
    private final CommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;

    private final ActuatorState<OnOffState> actuatorState;

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
    public ActuatorState getState() {
        return actuatorState;
    };

    @Override
    public void acceptMessage(Message message) {
        consumeMessage(message, OnOffState.class, this::consumeOnOffMessage);
    }

    void acceptCommand(SwitchMultilevelReport report) {
        this.actuatorState.updatePayload(OnOffState.of(report.getCurrentValue() != 0));
    }

    private void consumeOnOffMessage(Message message) {
        try {
            OnOffState updatedState = determineOnOffState(message, actuatorState.getPayload());
            ZWaveControlledCommand command = updatedState.isOn() ? commandBuilder.v2().buildSetMaximumCommand(switchDuration)
                : commandBuilder.v2().buildSetMinimumCommand(switchDuration);
            commandSender.enqueueCommand(nodeId, command);
            actuatorState.updatePayload(updatedState);
        } catch(JWaveZException e) {
            throw new MessageProcessingException("Command send failed!", e);
        }
    }
}
