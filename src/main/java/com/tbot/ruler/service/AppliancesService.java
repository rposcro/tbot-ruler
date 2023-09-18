package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.service.things.AppliancesLifetimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppliancesService {

    @Autowired
    private AppliancesLifetimeService appliancesLifetimeService;

    public List<Appliance> findAllAppliances() {
        return appliancesLifetimeService.getAllAppliances();
    }

    public Optional<Appliance> findApplianceByUuid(String applianceUuid) {
        return Optional.ofNullable(appliancesLifetimeService.getApplianceByUuid(applianceUuid));
    }
}
