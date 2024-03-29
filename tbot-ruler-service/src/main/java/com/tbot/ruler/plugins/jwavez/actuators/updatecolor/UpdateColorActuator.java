package com.tbot.ruler.plugins.jwavez.actuators.updatecolor;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.RGBWColor;
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
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Byte.toUnsignedInt;

@Slf4j
@Getter
public class UpdateColorActuator extends AbstractSubject implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateColorConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final CommandSender commandSender;

    private final SwitchColorCommandBuilder commandBuilder;
    private final long pollIntervalMilliseconds;
    private final ColorMode colorMode;

    private final Collection<JobBundle> jobBundles;

    private final HashMap<Integer, Integer> collectedComponentsReports = new HashMap<>();

    @Builder
    public UpdateColorActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull CommandSender commandSender,
            @NonNull UpdateColorConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
    ) {
        super(uuid, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.colorMode = ColorMode.valueOf(configuration.getColorMode());
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchColorCommandBuilder();
        this.jobBundles = jobBundles();
    }

    public void acceptCommand(SwitchColorReport command) {
        log.debug("Color component {} report received for node {}", command.getColorComponentId(), command.getSourceNodeId().getId());
        collectedComponentsReports.put((int) command.getColorComponentId(), (int) command.getCurrentValue());
        log.debug("Current state is: " + collectedComponentsReports.keySet().stream().map(code -> "" + code).collect(Collectors.joining(", ")));
        if (allComponentsCollected()) {
            log.debug("All color components collected for node {}, sending color update message", configuration.getNodeId());
            Message message = generateMessage();
            messagePublisher.publishMessage(message);
            collectedComponentsReports.clear();
        }
    }

    private boolean allComponentsCollected() {
        return IntStream.of(colorMode.getComponentCodes())
                .allMatch(componentCode -> collectedComponentsReports.containsKey(componentCode));
    }

    private Message generateMessage() {
        return Message.builder()
                .senderId(this.getUuid())
                .payload(RGBWColor.of(
                        collectedComponentsReports.get(toUnsignedInt(ColorComponent.RED.getCode())),
                        collectedComponentsReports.get(toUnsignedInt(ColorComponent.GREEN.getCode())),
                        collectedComponentsReports.get(toUnsignedInt(ColorComponent.BLUE.getCode())),
                        collectedComponentsReports.get(toUnsignedInt(ColorComponent.WARM_WHITE.getCode()))
                ))
                .build();
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
            private final String name = UpdateColorActuator.class.getSimpleName() + "-Job@" + UpdateColorActuator.this.getUuid();

            @Override
            public void doJob() {
                UpdateColorActuator.this.collectedComponentsReports.clear();
                log.debug("Sending color components report request for node " + configuration.getNodeId());
                IntStream.of(colorMode.getComponentCodes()).forEach(componentCode -> {
                    try {
                        commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand((byte) componentCode));
                    } catch (MessageProcessingException e) {
                        log.warn("Failed to send request for color component {} from node {}", componentCode, configuration.getNodeId());
                    }
                });
            }
        };
    }
}
