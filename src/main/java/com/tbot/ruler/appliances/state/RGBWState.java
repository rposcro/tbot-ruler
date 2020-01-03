package com.tbot.ruler.appliances.state;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RGBWState implements State {

    private short red;
    private short green;
    private short blue;
    private short white;

    public static RGBWState of(int red, int green, int blue, int white) {
        return new RGBWState((short) red, (short) green, (short) blue, (short) white);
    }
}
