package com.tbot.ruler.controller;

import com.tbot.ruler.appliances.state.OnOffState;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.service.AppliancesStateService;
import com.tbot.ruler.things.ApplianceId;
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

    @PatchMapping(value = "/{applianceId}/state/on-off")
    public ResponseEntity<?> changeBinaryState(
        @PathVariable("applianceId") ApplianceId applianceId,
        @RequestBody OnOffState state) {
        log.debug("Requested on-off {} state change for {}", state.isOn(), applianceId.getValue());
        appliancesStateService.updateApplianceState(applianceId, state);
        return response(ResponseEntity.noContent()).build();
    }
}
