package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.message.MessagePayload;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.message.payloads.UpdateRequestPayload;
import com.tbot.ruler.plugins.deputy.model.BinOutState;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.rest.RestResponse;
import com.tbot.ruler.things.ItemId;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import com.tbot.ruler.message.MessagePublisher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

@Slf4j
@Builder
@AllArgsConstructor
public class BinaryActuatorConsumer implements Consumer<Message> {

    @NonNull private MessagePublisher messagePublisher;
    @NonNull private ItemId actuatorId;
    @NonNull private RestPatchCommand patchCommand;
    @NonNull private RestGetCommand getCommand;

    @Override
    public void accept(Message message) {
        MessagePayload payload = message.getPayload();
        if (payload instanceof BooleanUpdatePayload) {
            handleBooleanUpdate(payload.ensureMessageType());
        } else if (payload instanceof UpdateRequestPayload) {
            handleUpdateRequest(payload.ensureMessageType());
        }
    }

    private void handleBooleanUpdate(BooleanUpdatePayload payload) {
        try {
            Map<String, String> reqParams = Collections.singletonMap("state", payload.isState() ? "on" : "off");
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

    private void handleUpdateRequest(BooleanUpdatePayload payload) {
        try {
            ResponseEntity<BinOutState> response = getCommand.sendGet(BinOutState.class);

            if (response.getStatusCodeValue() != 200) {
                log.info("Bin-out state read request failed! " + response.getStatusCode());
                throw new MessageProcessingException("Failed to request bin-out state value! Status code returned: " + response.getStatusCode());
            }

            Message message = Message.builder()
                .senderId(actuatorId)
                .payload(BooleanUpdatePayload.builder()
                    .state("on".equalsIgnoreCase(response.getBody().getState()))
                    .build())
                .build();
            messagePublisher.accept(message);
        }
        catch(RestClientException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new MessageProcessingException(e);
        }
    }
}
