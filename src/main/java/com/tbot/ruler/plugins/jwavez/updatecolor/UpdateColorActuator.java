package com.tbot.ruler.plugins.jwavez.updatecolor;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.builders.switchcolor.SwitchColorCommandBuilder;
import com.rposcro.jwavez.core.commands.supported.switchcolor.SwitchColorReport;
import com.rposcro.jwavez.core.model.ColorComponent;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.RGBWColor;
import com.tbot.ruler.plugins.jwavez.JWaveZCommandSender;
import com.tbot.ruler.things.AbstractItem;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.thread.TaskTrigger;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Byte.toUnsignedInt;

@Slf4j
@Getter
public class UpdateColorActuator extends AbstractItem implements Actuator {

    private final static int MIN_POLL_INTERVAL = 120;

    private final UpdateColorConfiguration configuration;
    private final MessagePublisher messagePublisher;
    private final JWaveZCommandSender commandSender;

    private final SwitchColorCommandBuilder commandBuilder;
    private final long pollIntervalMilliseconds;
    private final ColorMode colorMode;

    private final HashMap<Integer, Integer> collectedComponentsReports = new HashMap<>();

    @Builder
    public UpdateColorActuator(
            @NonNull String id,
            @NonNull String name,
            String description,
            @NonNull MessagePublisher messagePublisher,
            @NonNull JWaveZCommandSender commandSender,
            @NonNull UpdateColorConfiguration configuration,
            @NonNull JwzApplicationSupport applicationSupport
    ) {
        super(id, name, description);
        this.messagePublisher = messagePublisher;
        this.commandSender = commandSender;
        this.configuration = configuration;
        this.pollIntervalMilliseconds = configuration.getPollStateInterval() <= 0 ? 0 : 1000 * Math.max(MIN_POLL_INTERVAL, configuration.getPollStateInterval());
        this.colorMode = ColorMode.valueOf(configuration.getColorMode());
        this.commandBuilder = applicationSupport.controlledCommandFactory().switchColorCommandBuilder();
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
            this.collectedComponentsReports.clear();
            log.debug("Sending color components report request for node " + configuration.getNodeId());
            IntStream.of(colorMode.getComponentCodes()).forEach(componentCode -> {
                try {
                    commandSender.enqueueCommand(new NodeId((byte) configuration.getNodeId()), commandBuilder.v1().buildGetCommand((byte) componentCode));
                } catch (MessageProcessingException e) {
                    log.warn("Failed to send request for color component {} from node {}", componentCode, configuration.getNodeId());
                }
            });
        });
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }

    public boolean acceptsReportCommand(byte nodeId) {
        return nodeId == (byte) configuration.getNodeId();
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

    @Override
    public void acceptMessage(Message message) {
    }
}
