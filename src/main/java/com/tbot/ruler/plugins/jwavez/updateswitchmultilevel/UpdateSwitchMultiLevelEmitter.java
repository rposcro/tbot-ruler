package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.rposcro.jwavez.core.commands.controlled.builders.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
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
public class UpdateSwitchMultiLevelEmitter extends AbstractItem implements Emitter {

    private final static int MIN_POLL_INTERVAL = 120;

    private UpdateSwitchMultiLevelEmitterConfiguration configuration;
    private MessagePublisher messagePublisher;
    private JWaveZCommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder = new SwitchMultiLevelCommandBuilder();
    private long pollIntervalMilliseconds;

    @Builder
    public UpdateSwitchMultiLevelEmitter(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull UpdateSwitchMultiLevelEmitterConfiguration configuration
    ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
    }

    @Override
    public Optional<TaskTrigger> getTaskTrigger() {
        if (configuration.getPollStateInterval() == 0) {
            return Optional.empty();
        }
        return Optional.of(context -> new Date(System.currentTimeMillis() + pollIntervalMilliseconds));
    }

    @Override
    public Optional<Runnable> getTriggerableTask() {
        return Optional.of(() -> {
            log.debug("Sending multi level report request for node " + configuration.getNodeId());
            commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.buildGetCommand());
        });
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }

    public boolean acceptsReportCommand(byte nodeId) {
        return nodeId == (byte) configuration.getNodeId();
    }

    public void acceptCommand(SwitchMultilevelReport command) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getId())
                .payload(OnOffState.of(command.getCurrentValue() != 0))
                .build());
    }
}
