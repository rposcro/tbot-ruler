package com.tbot.ruler.plugins.jwavez.updateswitchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Getter
public class UpdateSwitchMultiLevelActuator extends AbstractItem implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateSwitchMultiLevelConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final JWaveZCommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;
    private final long pollIntervalMilliseconds;

    private final Collection<Task> asynchronousTasks;

    @Builder
    public UpdateSwitchMultiLevelActuator(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull UpdateSwitchMultiLevelConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
            ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchMultiLevelCommandBuilder();
        this.asynchronousTasks = asynchronousTasks();
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    public boolean acceptsReportCommand(byte nodeId) {
        return nodeId == (byte) configuration.getNodeId();
    }

    public void acceptCommand(SwitchMultilevelReport command) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(OnOffState.of(command.getCurrentValue() != 0))
                .build());
    }

    @Override
    public void acceptMessage(Message message) {
    }

    private Collection<Task> asynchronousTasks() {
        if (configuration.getPollStateInterval() == 0) {
            return Collections.emptySet();
        } else {
            Runnable runnable = () -> {
                log.debug("Sending multi level report request for node " + configuration.getNodeId());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand());
            };
            return Collections.singleton(Task.triggerableTask(runnable, pollIntervalMilliseconds));
        }
    }
}
