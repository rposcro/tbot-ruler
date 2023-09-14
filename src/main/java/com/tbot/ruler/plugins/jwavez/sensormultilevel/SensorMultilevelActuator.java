package com.tbot.ruler.plugins.jwavez.sensormultilevel;

import com.rposcro.jwavez.core.commands.supported.sensormultilevel.SensorMultilevelReport;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.messages.model.Message;
import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.Measure;
import com.tbot.ruler.model.MeasureQuantity;
import com.tbot.ruler.things.Actuator;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class SensorMultilevelActuator implements Actuator {

    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private MessagePublisher messagePublisher;

    public void publishMessage(SensorMultilevelReport report) {
        messagePublisher.publishMessage(Message.builder()
                .senderId(id)
                .payload(Measure.builder()
                        .quantity(MeasureQuantity.Temperature)
                        .unit("\u00baC")
                        .decimals((short) (report.getPrecision() & 0xff))
                        .value(report.getMeasureValue())
                        .build())
                .build());
    }

    @Override
    public void acceptDeliveryReport(MessageDeliveryReport deliveryReport) {
    }

    @Override
    public void acceptMessage(Message message) {
    }
}
