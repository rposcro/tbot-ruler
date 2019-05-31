package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.rest.RestResponse;
import com.tbot.ruler.signals.OnOffSignalValue;
import com.tbot.ruler.signals.SignalValue;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.ActuatorException;
import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.ActuatorMetadata;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

@Slf4j
@Builder
@AllArgsConstructor
public class BinOutActuator implements Actuator {

    private RestPatchCommand patchCommand;
    private RestGetCommand getCommand;
    @Getter
    private ActuatorMetadata metadata;

    @Override
    public void changeState(SignalValue signal) throws ActuatorException {
        try {
            OnOffSignalValue onOffSignal = (OnOffSignalValue) signal;
            Map<String, String> reqParams = Collections.singletonMap("state", stateValue(onOffSignal));
            RestResponse response = patchCommand.sendPatch(reqParams);

            if (response.getStatusCode() != 200) {
                log.info("Bin-out state change request failed! " + response.getStatusCode());
                throw new ActuatorException("Failed to request bin-out state change! Status code returned: " + response.getStatusCode());
            }
        }
        catch(RestClientException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new ActuatorException(e);
        }
    }

    @Override
    public SignalValue readState() throws ActuatorException {
        try {
            ResponseEntity<BinOutState> response = getCommand.sendGet(BinOutState.class);

            if (response.getStatusCodeValue() != 200) {
                log.info("Bin-out state read request failed! " + response.getStatusCode());
                throw new ActuatorException("Failed to request bin-out state value! Status code returned: " + response.getStatusCode());
            }

            BinOutState state = response.getBody();
            return new OnOffSignalValue(state.getState());
        }
        catch(RestClientException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new ActuatorException(e);
        }
    }

    @Override
    public ActuatorId getId() {
        return metadata.getId();
    }

    private String stateValue(OnOffSignalValue onOffSignal) {
        return onOffSignal.isOnSignal() ? "on" : "off";
    }
}
