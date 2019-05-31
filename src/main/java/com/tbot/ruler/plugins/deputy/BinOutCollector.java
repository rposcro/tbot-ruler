package com.tbot.ruler.plugins.deputy;

import java.util.Collections;
import java.util.Map;

import com.tbot.ruler.rest.RestResponse;
import com.tbot.ruler.signals.ApplianceSignal;
import com.tbot.ruler.things.CollectorId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;

import com.tbot.ruler.rest.RestPatchCommand;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.CollectorException;
import com.tbot.ruler.things.CollectorMetadata;
import com.tbot.ruler.signals.OnOffSignalValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Slf4j
@Builder
@AllArgsConstructor
public class BinOutCollector implements Collector {

    private RestPatchCommand patchCommand;
    @Getter
    private CollectorId id;
    @Getter
    private CollectorMetadata metadata;

    @Override
    public void collectSignal(ApplianceSignal signal) throws CollectorException {
        try {
            OnOffSignalValue onOffSignal = (OnOffSignalValue) signal.getSignalValue();
            Map<String, String> reqParams = Collections.singletonMap("state", stateValue(onOffSignal));
            RestResponse response = patchCommand.sendPatch(reqParams);

            if (response.getStatusCode() != 200) {
                log.info("Bin-out state change request failed! " + response.getStatusCode());
                throw new CollectorException("Failed to request bin-out state change! Status code returned: " + response.getStatusCode());
            }
        }
        catch(RestClientException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new CollectorException(e);
        }
    }

    private String stateValue(OnOffSignalValue onOffSignal) {
        return onOffSignal.isOnSignal() ? "ON" : "OFF";
    }
}
