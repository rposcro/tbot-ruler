package com.tbot.ruler.model;

import com.tbot.ruler.messages.model.MessagePayload;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Measure implements MessagePayload {

    private MeasureQuantity quantity;
    private String unit;
    private long value;
    private short precision;
}
