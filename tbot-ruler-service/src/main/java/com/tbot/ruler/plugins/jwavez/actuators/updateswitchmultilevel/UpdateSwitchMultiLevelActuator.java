package com.tbot.ruler.plugins.jwavez.actuators.updateswitchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.builders.switchmultilevel.SwitchMultiLevelCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchmultilevel.SwitchMultilevelReport;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Getter
public class UpdateSwitchMultiLevelActuator extends AbstractSubject implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateSwitchMultiLevelConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final CommandSender commandSender;

    private final SwitchMultiLevelCommandBuilder commandBuilder;
    private final long pollIntervalMilliseconds;

    private final Collection<JobBundle> jobBundles;

    @Builder
    public UpdateSwitchMultiLevelActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull CommandSender commandSender,
            @NonNull UpdateSwitchMultiLevelConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
            ) {
        super(uuid, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchMultiLevelCommandBuilder();
        this.jobBundles = jobBundles();
    }

    public void acceptCommand(SwitchMultilevelReport command) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(OnOffState.of(command.getCurrentValue() != 0))
                .build());
    }

    private Collection<JobBundle> jobBundles() {
        if (pollIntervalMilliseconds == 0) {
            return Collections.emptySet();
        } else {
            return Collections.singleton(JobBundle.periodicalJobBundle(updateRequestJob(), pollIntervalMilliseconds));
        }
    }

    private Job updateRequestJob() {
        return new Job() {
            @Getter
            private final String name = UpdateSwitchMultiLevelActuator.class.getSimpleName() + "-Job@" + UpdateSwitchMultiLevelActuator.this.getUuid();

            @Override
            public void doJob() {
                log.debug("Sending multi level report request for node " + configuration.getNodeId());
                commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand());
            }
        };
    }
}
