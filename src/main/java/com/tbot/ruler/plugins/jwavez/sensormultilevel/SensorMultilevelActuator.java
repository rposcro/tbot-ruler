package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import com.tbot.ruler.broker.payload.Measure;
import com.tbot.ruler.broker.payload.MeasureQuantity;
import com.tbot.ruler.subjects.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class SensorMultilevelActuator implements Actuator {

    @NonNull
    private String uuid;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private MessagePublisher messagePublisher;

    public void publishMessage(SensorMultilevelReport report) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(uuid)
                .payload(Measure.builder()
                        .quantity(MeasureQuantity.Temperature)
                        .unit("\u00baC")
                        .decimals((short) (report.getPrecision() & 0xff))
                        .value(report.getMeasureValue())
                        .build())
                .build());
    }

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
    }

    @Override
    public void acceptMessage(Message message) {
    }
}
