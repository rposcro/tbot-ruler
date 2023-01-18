package com.tbot.ruler.messages.payloads;

import lombok.NoArgsConstructor;

/**
 * Item receiving this payload is expected to emit its value update message.
 * For example binary output actuator to emit BooleanUpdatePayload message.
 */
@NoArgsConstructor
public class UpdateRequestPayload {

    public final static UpdateRequestPayload UPDATE_REQUEST_PAYLOAD = new UpdateRequestPayload();
}
