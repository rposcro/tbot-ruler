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
public class BooleanUpdatePayload implements MessagePayload {

    public final static BooleanUpdatePayload UPDATE_TRUE = new BooleanUpdatePayload(true);
    public final static BooleanUpdatePayload UPDATE_FALSE = new BooleanUpdatePayload(false);

    private boolean state;

    public static BooleanUpdatePayload of(boolean value) {
        return value ? UPDATE_TRUE : UPDATE_FALSE;
    }
}
