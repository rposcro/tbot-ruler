package com.tbot.ruler.message.payloads;

import com.tbot.ruler.message.MessagePayload;
import lombok.NoArgsConstructor;

/**
 * Item receiving this payload is expected to emit its value update message.
 * For example binary output actuator to emit BooleanUpdatePayload message.
 */
@NoArgsConstructor
public class UpdateRequestPayload implements MessagePayload {

    public final static UpdateRequestPayload UPDATE_REQUEST_PAYLOAD = new UpdateRequestPayload();
}
