package com.tbot.ruler.controller;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.controller.payload.ApplianceResponse;
import com.tbot.ruler.exceptions.ServiceException;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import com.tbot.ruler.service.AppliancesService;
import com.tbot.ruler.service.admin.AppliancesAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/appliances")
public class AppliancesController extends AbstractController {

    @Autowired
    private AppliancesService appliancesService;

    @Autowired
    private AppliancesAdminService adminService;

    @GetMapping(value = "")
    public ResponseEntity<List<ApplianceResponse>> getAll() {
        List<ApplianceResponse> entities = appliancesService.findAllAppliances().stream()
            .map(this::fromAppliance)
            .collect(Collectors.toList());
        return response(ResponseEntity.ok())
            .body(entities);
    }

    @GetMapping(value = "/{applianceId}")
    public ResponseEntity<ApplianceResponse> getAppliance(@PathVariable("applianceId") String applianceId) {
        Appliance appliance = appliancesService.findApplianceByUuid(applianceId)
            .orElseThrow(() -> new ServiceException("Unexpected missing delivery report without exception!"));
        ApplianceResponse entity = fromAppliance(appliance);
        return response(ResponseEntity.ok())
            .body(entity);
    }

    private ApplianceResponse fromAppliance(Appliance appliance) {
        ApplianceEntity entity = adminService.applianceByUuid(appliance.getUuid());
        return ApplianceResponse.builder()
            .id(appliance.getUuid())
            .name(entity.getName())
            .description(entity.getDescription())
            .stateValue(appliance.getState().orElse(null))
            .build();
    }
}
