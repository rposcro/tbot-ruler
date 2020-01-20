package com.tbot.ruler.plugins.deputy;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.rest.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;

import com.tbot.ruler.rest.RestPatchCommand;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Slf4j
@Builder
@AllArgsConstructor
public class BinOutCollectorConsumer implements Consumer<Message> {

    private RestPatchCommand patchCommand;

    @Override
    public void accept(Message message) {
        BooleanUpdatePayload messagePayload = message.getPayload().ensureMessageType();
        try {
            Map<String, String> reqParams = Collections.singletonMap("state", stateValue(messagePayload));
            RestResponse response = patchCommand.sendPatch(reqParams);

            if (response.getStatusCode() != 200) {
                log.info("Bin-out state change request failed! " + response.getStatusCode());
                throw new MessageProcessingException("Failed to request bin-out state change! Status code returned: " + response.getStatusCode());
            }
        }
        catch(RestClientException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new MessageProcessingException(e);
        }
    }

    private String stateValue(BooleanUpdatePayload messagePayload) {
        return messagePayload.isState() ? "ON" : "OFF";
    }
}
