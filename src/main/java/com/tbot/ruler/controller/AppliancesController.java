package com.tbot.ruler.controller;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.controller.entity.ApplianceEntity;
import com.tbot.ruler.exceptions.ServiceException;
import com.tbot.ruler.service.AppliancesService;
import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.things.ApplianceId;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private AppliancesAdminService adminService;

    @GetMapping(value = "")
    public ResponseEntity<List<ApplianceEntity>> getAll() {
        List<ApplianceEntity> entities = appliancesService.allAppliances().stream()
            .map(this::fromAppliance)
            .collect(Collectors.toList());
        return response(ResponseEntity.ok())
            .body(entities);
    }

    @GetMapping(value = "/{applianceId}")
    public ResponseEntity<ApplianceEntity> getAppliance(@PathVariable("applianceId") ApplianceId applianceId) {
        Appliance appliance = appliancesService.applianceById(applianceId)
            .orElseThrow(() -> new ServiceException("Unexpected missing delivery report without exception!"));
        ApplianceEntity entity = fromAppliance(appliance);
        return response(ResponseEntity.ok())
            .body(entity);
    }

    private ApplianceEntity fromAppliance(Appliance appliance) {
        ApplianceDTO dto = adminService.applianceDTOById(appliance.getId());
        return ApplianceEntity.builder()
            .id(appliance.getId().getValue())
            .name(dto.getName())
            .description(dto.getDescription())
            .stateValue(appliance.getState().orElse(null))

            .build();
    }
}
