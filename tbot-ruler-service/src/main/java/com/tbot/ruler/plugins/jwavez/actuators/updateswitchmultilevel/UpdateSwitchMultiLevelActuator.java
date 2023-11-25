package com.tbot.ruler.plugins.jwavez.actuators.updateswitchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.task.SubjectTask;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Getter
public class UpdateSwitchMultiLevelActuator extends AbstractSubject implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateSwitchMultiLevelConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final CommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;
    private final long pollIntervalMilliseconds;

    private final Collection<SubjectTask> asynchronousSubjectTasks;

    @Builder
    public UpdateSwitchMultiLevelActuator(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull CommandSender commandSender,
            @NonNull UpdateSwitchMultiLevelConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
            ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchMultiLevelCommandBuilder();
        this.asynchronousSubjectTasks = asynchronousTasks();
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
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

    private Collection<SubjectTask> asynchronousTasks() {
        if (pollIntervalMilliseconds == 0) {
            return Collections.emptySet();
        } else {
            Runnable runnable = () -> {
                log.debug("Sending multi level report request for node " + configuration.getNodeId());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand());
            };
            return List.of(
                    SubjectTask.triggerableTask(runnable, pollIntervalMilliseconds),
                    SubjectTask.startUpTask(runnable));
        }
    }
}
