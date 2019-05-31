package com.tbot.ruler.controller;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.controller.entity.ApplianceEntity;
import com.tbot.ruler.exceptions.SignalException;
import com.tbot.ruler.service.AppliancesStateService;
import com.tbot.ruler.service.AppliancesService;
import com.tbot.ruler.signals.OnOffSignalValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = ControllerConstants.ENDPOINT_APPLIANCES)
public class AppliancesController extends AbstractController {

    @Autowired
    private AppliancesService appliancesService;

    @Autowired
    private AppliancesStateService appliancesStateService;

    @GetMapping(value="")
    public ResponseEntity<List<ApplianceEntity>> getAll() {
        List<ApplianceEntity> entities = appliancesService.allAppliances().stream()
            .map((appliance) -> new ApplianceEntity(appliance, Collections.emptyMap()))
            .collect(Collectors.toList());
        return response(ResponseEntity.ok())
            .body(entities);
    }

    @GetMapping(value = "/{applianceId}")
    public ResponseEntity<ApplianceEntity> getAppliance(@PathVariable("applianceId") String applianceIdValue) {
        Appliance appliance = appliancesService.applianceById(new ApplianceId(applianceIdValue));
        if (appliance == null) {
            return ResponseEntity.noContent().build();
        }
        ApplianceEntity entity = new ApplianceEntity(appliance, Collections.emptyMap());
        return response(ResponseEntity.ok())
            .body(entity);
    }

    @PatchMapping(value = "/{applianceId}/state")
    public HttpStatus changeOnOffState(
        @PathVariable("applianceId") String applianceIdValue,
        @RequestParam("state") String state) throws SignalException {
        ApplianceId applianceId = new ApplianceId(applianceIdValue);
        OnOffSignalValue stateValue = "on".equalsIgnoreCase(state) ? OnOffSignalValue.ON_SIGNAL_VALUE : OnOffSignalValue.OFF_SIGNAL_VALUE;
        appliancesStateService.changeStateValue(applianceId, stateValue);
        return HttpStatus.OK;
    }
}
