package com.tbot.ruler.message.payloads;

import com.tbot.ruler.message.MessagePayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RGBWUpdatePayload implements MessagePayload {

    private short red;
    private short green;
    private short blue;
    private short white;

    public static RGBWUpdatePayload of(int red, int green, int blue, int white) {
        return new RGBWUpdatePayload((short) red, (short) green, (short) blue, (short) white);
    }
}
