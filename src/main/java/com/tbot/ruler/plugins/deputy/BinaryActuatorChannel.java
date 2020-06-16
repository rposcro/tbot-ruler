package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.exceptions.RestRequestException;
import com.tbot.ruler.plugins.deputy.model.BinOutState;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.rest.RestResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Builder
@AllArgsConstructor
public class BinaryActuatorChannel {

    private static String VALUE_ON = "on";
    private static String VALUE_OFF = "off";

    @NonNull private RestPatchCommand patchCommand;
    @NonNull private RestGetCommand getCommand;

    public void updateState(boolean state) {
        try {
            Map<String, String> reqParams = Collections.singletonMap("state", state ? VALUE_ON : VALUE_OFF);
            RestResponse response = patchCommand.sendPatch(reqParams);

            if (response.getStatusCode() != 200) {
                log.info("Bin-out state change request failed! " + response.getStatusCode());
                throw new MessageProcessingException("Failed to request bin-out state change! Status code returned: " + response.getStatusCode());
            }
        }
        catch(RestRequestException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new MessageProcessingException(e);
        }
    }

    public boolean requestState() {
        try {
            RestResponse<BinOutState> response = getCommand.sendGet(BinOutState.class);

            if (response.getStatusCode() != 200) {
                log.info("Bin-out state read request failed! " + response.getStatusCode());
                throw new MessageProcessingException("Failed to request bin-out state value! Status code returned: " + response.getStatusCode());
            }

            return VALUE_ON.equalsIgnoreCase(response.getBody().getState());
        }
        catch(RestRequestException e) {
            log.info("Bin-out state read request failed! " + e.getMessage());
            throw new MessageProcessingException(e);
        }
    }
}
