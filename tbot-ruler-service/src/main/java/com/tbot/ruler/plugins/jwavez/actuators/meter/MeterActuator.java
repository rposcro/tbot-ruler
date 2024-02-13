package com.tbot.ruler.plugins.jwavez.actuators.meter;

import com.rposcro.jwavez.core.commands.supported.meter.MeterReport;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.Measure;
import com.tbot.ruler.broker.payload.MeasureQuantity;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class MeterActuator extends AbstractActuator {

    private MessagePublisher messagePublisher;
    private final ActuatorState<Measure> actuatorState;

    @Builder
    public MeterActuator(
        @NonNull String uuid,
        @NonNull String name,
        String description,
        @NonNull MessagePublisher messagePublisher
    ) {
        super(uuid, name, description);
        this.messagePublisher = messagePublisher;
        this.actuatorState = ActuatorState.<Measure>builder()
                .actuatorUuid(uuid)
                .build();
    }

    public void acceptMeterReport(MeterReport report) {
        log.debug("Jwz Plugin: Received meter report from {} for {}", report.getSourceNodeId(), getUuid());
        Measure measure = extractMeasure(report);
        log.debug("Jwz Plugin: Measure is {}", measure);
        messagePublisher.publishMessage(Message.builder()
                .senderId(getUuid())
                .payload(measure)
                .build());
        actuatorState.updatePayload(measure);

    }

    private Measure extractMeasure(MeterReport report) {
        return Measure.builder()
                .quantity(MeasureQuantity.Temperature)
                .unit("\u00baC")
                .decimals((short) (report.getPrecision() & 0xff))
                .value(report.getMeasure())
                .build();
    }
}
