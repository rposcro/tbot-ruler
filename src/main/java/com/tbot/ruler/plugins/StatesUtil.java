package com.tbot.ruler.plugins;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.exceptions.MessageUnsupportedException;

import static com.tbot.ruler.broker.payload.OnOffState.STATE_OFF;
import static com.tbot.ruler.broker.payload.OnOffState.STATE_ON;

public class StatesUtil {

    public static OnOffState determineOnOffState(Message updateMessage, OnOffState existingState) {
        if (updateMessage.isPayloadAs(BinaryStateClaim.class)) {
            BinaryStateClaim claim = updateMessage.getPayloadAs(BinaryStateClaim.class);
            if (claim.isToggle()) {
                return existingState != null && existingState.isOn() ? STATE_OFF : STATE_ON;
            } else {
                return OnOffState.of(claim.isSetOn());
            }
        } else if (updateMessage.isPayloadAs(OnOffState.class)) {
            return updateMessage.getPayloadAs(OnOffState.class);
        } else {
            throw new MessageUnsupportedException("Unsupported message payload of class " + updateMessage.getPayload().getClass());
        }
    }
}
