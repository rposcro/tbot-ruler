package com.tbot.ruler.plugins.jwavez.actuators.updateswitchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.commands.controlled.builders.multichannel.MultiChannelCommandBuilder;
import com.rposcro.jwavez.core.commands.controlled.builders.switchbinary.SwitchBinaryCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.binaryswitch.BinarySwitchReport;
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
public class UpdateSwitchBinaryActuator extends AbstractSubject implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateSwitchBinaryConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final CommandSender commandSender;

    private final SwitchBinaryCommandBuilder commandBuilder;
    private final MultiChannelCommandBuilder multiChannelCommandBuilder;
    private final long pollIntervalMilliseconds;
    private final boolean multiChannelOn;

    private final Collection<JobBundle> jobBundles;

    @Builder
    public UpdateSwitchBinaryActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull CommandSender commandSender,
            @NonNull UpdateSwitchBinaryConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
    ) {
        super(uuid, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.multiChannelOn = configuration.getNodeEndPointId() >= 0;
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchBinaryCommandBuilder();
        this.multiChannelCommandBuilder = applicationSupport.controlledCommandFactory().multiChannelCommandBuilder();
        this.jobBundles = jobBundles();
    }

    public void acceptCommand(BinarySwitchReport report) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(this.getUuid())
                .payload(OnOffState.of(report.getValue() != 0))
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
            private final String name = UpdateSwitchBinaryActuator.class.getSimpleName() + "-Job@" + UpdateSwitchBinaryActuator.this.getUuid();

            @Override
            public void doJob() {
                if (multiChannelOn) {
                    log.debug("Sending multi channel switch binary report request for node {} endPoint {}",
                            configuration.getNodeId(), configuration.getNodeEndPointId());
                    ZWaveControlledCommand command = multiChannelCommandBuilder.v3().encapsulateCommand(
                            (byte) 1, (byte) configuration.getNodeEndPointId(), commandBuilder.v1().buildGetCommand());
                    commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), command);
                } else {
                    log.debug("Sending switch binary report request for node " + configuration.getNodeId());
                    commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand());
                }
            }
        };
    }
}
