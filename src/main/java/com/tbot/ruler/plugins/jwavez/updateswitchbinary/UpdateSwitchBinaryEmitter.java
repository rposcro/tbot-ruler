package com.tbot.ruler.plugins.jwavez.updateswitchbinary;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
public class UpdateSwitchBinaryEmitter extends AbstractItem implements Emitter {

    private final static int MIN_POLL_INTERVAL = 120;

    private UpdateSwitchBinaryEmitterConfiguration configuration;
    private MessagePublisher messagePublisher;
    private JWaveZCommandSender commandSender;

    private final SwitchBinaryCommandBuilder commandBuilder;
    private final MultiChannelCommandBuilder multiChannelCommandBuilder;
    private final long pollIntervalMilliseconds;
    private final boolean multiChannelOn;

    @Builder
    public UpdateSwitchBinaryEmitter(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull UpdateSwitchBinaryEmitterConfiguration configuration
    ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.multiChannelOn = configuration.getEndPointId() >= 0;
        this.commandBuilder = new SwitchBinaryCommandBuilder();
        this.multiChannelCommandBuilder = new MultiChannelCommandBuilder();
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
                ZWaveControlledCommand command = multiChannelCommandBuilder.encapsulateCommand(
                        (byte) 1, (byte) configuration.getEndPointId(), commandBuilder.buildGetCommand());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), command);
            } else {
                log.debug("Sending switch binary report request for node " + configuration.getNodeId());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.buildGetCommand());
            }
        });
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }

    public void acceptCommand(BinarySwitchReport report) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getId())
                .payload(OnOffState.of(report.getValue() != 0))
                .build());
    }
}
