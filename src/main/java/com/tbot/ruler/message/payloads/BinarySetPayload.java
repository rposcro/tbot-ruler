package com.tbot.ruler.message.payloads;

import com.tbot.ruler.message.MessagePayload;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BinarySetPayload implements MessagePayload {

    private boolean on;

    @Builder
    public BinarySetPayload(boolean on) {
        this.on = on;
    }
}
