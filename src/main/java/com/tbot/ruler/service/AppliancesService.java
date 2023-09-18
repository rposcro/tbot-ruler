package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.configuration.AppliancesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppliancesService {

    @Autowired
    private AppliancesConfiguration appliancesConfiguration;

    public List<Appliance> allAppliances() {
        return appliancesConfiguration.appliances();
    }

    public Optional<Appliance> applianceById(String applianceId) {
        return Optional.ofNullable(appliancesConfiguration.appliancesPerUuid().get(applianceId));
    }
}
