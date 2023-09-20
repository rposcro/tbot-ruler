package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessageDeliveryReport;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
public class UpdateSwitchBinaryActuator extends AbstractItem implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateSwitchBinaryConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final JWaveZCommandSender commandSender;

    private final SwitchBinaryCommandBuilder commandBuilder;
    private final MultiChannelCommandBuilder multiChannelCommandBuilder;
    private final long pollIntervalMilliseconds;
    private final boolean multiChannelOn;

    @Builder
    public UpdateSwitchBinaryActuator(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull UpdateSwitchBinaryConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
    ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.multiChannelOn = configuration.getEndPointId() >= 0;
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchBinaryCommandBuilder();
        this.multiChannelCommandBuilder = applicationSupport.controlledCommandFactory().multiChannelCommandBuilder();
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        if (configuration.getPollStateInterval() == 0) {
            return Optional.empty();
        }
        return Optional.of(context -> context.getLastScheduledExecutionTime() == null ?
                new Date() : new Date(System.currentTimeMillis() + pollIntervalMilliseconds));
    }

    @Override
    public Optional<Runnable> getTriggerableTask() {
        return Optional.of(() -> {
            if (multiChannelOn) {
                log.debug("Sending multi channel switch binary report request for node {} endPoint {}",
                        configuration.getNodeId(), configuration.getEndPointId());
                ZWaveControlledCommand command = multiChannelCommandBuilder.v3().encapsulateCommand(
                        (byte) 1, (byte) configuration.getEndPointId(), commandBuilder.v1().buildGetCommand());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), command);
            } else {
                log.debug("Sending switch binary report request for node " + configuration.getNodeId());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand());
            }
        });
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }

    public void acceptCommand(BinarySwitchReport report) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(OnOffState.of(report.getValue() != 0))
                .build());
    }

    @Override
    public void acceptMessage(Message message) {
    }
}
