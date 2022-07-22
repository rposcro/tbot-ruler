package com.tbot.ruler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class RGBWColor {

    private short red;
    private short green;
    private short blue;
    private short white;

    public static RGBWColor of(int red, int green, int blue, int white) {
        return new RGBWColor((short) red, (short) green, (short) blue, (short) white);
    }
}
