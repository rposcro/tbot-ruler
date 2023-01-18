package com.tbot.ruler.controller;

import com.tbot.ruler.messages.model.MessageDeliveryReport;
import com.tbot.ruler.model.OnOffState;
import com.tbot.ruler.model.RGBWColor;
import com.tbot.ruler.service.AppliancesStateService;
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
@RequestMapping(value = "/appliances")
public class StateChangeController extends AbstractController {

    @Autowired
    private AppliancesStateService appliancesStateService;

    @PatchMapping(value = "/{applianceId}/state/on-off")
    public ResponseEntity<MessageDeliveryReport> changeBinaryState(
        @PathVariable("applianceId") String applianceId,
        @RequestBody OnOffState onOffState) {
        log.debug("Requested on-off {} state change for {}", onOffState.isOn(), applianceId);
        MessageDeliveryReport report = appliancesStateService.updateApplianceState(applianceId, onOffState);
        return response(ResponseEntity.ok()).body(report);
    }

    @PatchMapping(value = "/{applianceId}/state/color")
    public ResponseEntity<MessageDeliveryReport> changeColorState(
        @PathVariable("applianceId") String applianceId,
        @RequestBody RGBWColor stateUpdate) {
        log.debug("Requested color change for {}", applianceId);
        MessageDeliveryReport report = appliancesStateService.updateApplianceState(applianceId, stateUpdate);
        return response(ResponseEntity.ok()).body(report);
    }
}
