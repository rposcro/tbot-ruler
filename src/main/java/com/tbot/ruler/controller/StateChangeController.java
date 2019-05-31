package com.tbot.ruler.controller;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.exceptions.SignalException;
import com.tbot.ruler.model.state.OnOffValue;
import com.tbot.ruler.service.AppliancesStateService;
import com.tbot.ruler.signals.OnOffSignalValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = ControllerConstants.ENDPOINT_APPLIANCES)
public class StateChangeController extends AbstractController {

    @Autowired
    private AppliancesStateService appliancesStateService;

    @PatchMapping(value = "/{applianceId}/state/onoff")
    public ResponseEntity<?> changeOnOffState(
        @PathVariable("applianceId") ApplianceId applianceId,
        @RequestBody OnOffValue stateValue) throws SignalException {
        log.debug("Requested onoff state change: {} {}", applianceId.getValue(), stateValue.isOn());
        appliancesStateService.changeStateValue(applianceId, stateValue.isOn() ? OnOffSignalValue.ON_SIGNAL_VALUE : OnOffSignalValue.OFF_SIGNAL_VALUE);
        return response(ResponseEntity.noContent()).build();
    }
}
