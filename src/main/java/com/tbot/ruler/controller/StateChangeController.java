package com.tbot.ruler.controller;

import com.tbot.ruler.message.DeliveryReport;
import com.tbot.ruler.message.payloads.BooleanUpdatePayload;
import com.tbot.ruler.message.payloads.RGBWUpdatePayload;
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
    public ResponseEntity<DeliveryReport> changeBinaryState(
        @PathVariable("applianceId") ApplianceId applianceId,
        @RequestBody BooleanUpdatePayload stateUpdate) {
        log.debug("Requested on-off {} state change for {}", stateUpdate.isState(), applianceId.getValue());
        DeliveryReport report = appliancesStateService.updateApplianceState(applianceId, stateUpdate);
        return response(ResponseEntity.ok()).body(report);
    }

    @PatchMapping(value = "/{applianceId}/state/color")
    public ResponseEntity<DeliveryReport> changeColorState(
        @PathVariable("applianceId") ApplianceId applianceId,
        @RequestBody RGBWUpdatePayload stateUpdate) {
        log.debug("Requested color change for {}", applianceId.getValue());
        DeliveryReport report = appliancesStateService.updateApplianceState(applianceId, stateUpdate);
        return response(ResponseEntity.ok()).body(report);
    }
}
