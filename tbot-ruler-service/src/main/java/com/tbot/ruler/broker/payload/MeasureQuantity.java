package com.tbot.ruler.broker.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MeasureQuantity {

    Temperature((short) 1);

    public static final int PRECISION_ANY = Integer.MIN_VALUE;

    private short precision;
}
