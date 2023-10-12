package com.tbot.ruler.plugins.jwavez.actuators.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.Measure;
import com.tbot.ruler.broker.payload.MeasureQuantity;
import com.tbot.ruler.subjects.AbstractActuator;
import com.tbot.ruler.subjects.ActuatorState;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SensorMultilevelActuator extends AbstractActuator {

    private final MessagePublisher messagePublisher;
    private final ActuatorState<Measure> actuatorState;

    @Builder
    public SensorMultilevelActuator(String uuid, String name, String description, MessagePublisher messagePublisher) {
        super(uuid, name, description);
        this.messagePublisher = messagePublisher;
        this.actuatorState = ActuatorState.<Measure>builder()
                .actuatorUuid(uuid)
                .build();
    }

    @Override
    public ActuatorState getState() {
        return this.actuatorState;
    }

    public void acceptCommand(SensorMultilevelReport report) {
        log.debug("Received multilevel report from {} for {}", report.getSourceNodeId(), getUuid());
        Measure measure = extractMeasure(report);
        messagePublisher.publishMessage(Message.builder()
                .senderId(getUuid())
                .payload(measure)
                .build());
        actuatorState.updatePayload(measure);
    }

    private Measure extractMeasure(SensorMultilevelReport report) {
        return Measure.builder()
                .quantity(MeasureQuantity.Temperature)
                .unit("\u00baC")
                .decimals((short) (report.getPrecision() & 0xff))
                .value(report.getMeasureValue())
                .build();
    }
}
