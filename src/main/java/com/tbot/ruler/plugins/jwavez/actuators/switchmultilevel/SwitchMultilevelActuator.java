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
import com.tbot.ruler.plugins.jwavez.controller.JWaveZCommandSender;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import static com.tbot.ruler.plugins.StatesUtil.determineOnOffState;

@Getter
public class SwitchMultilevelActuator implements Actuator {

    private final String uuid;
    private final String name;
    private final String description;

    private final byte switchDuration;
    private final NodeId nodeId;
    private final JWaveZCommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;

    private final ActuatorState<OnOffState> actuatorState;

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

    public void acceptCommand(SwitchMultilevelReport report) {
        this.actuatorState.updatePayload(OnOffState.of(report.getCurrentValue() != 0));
    }
}
