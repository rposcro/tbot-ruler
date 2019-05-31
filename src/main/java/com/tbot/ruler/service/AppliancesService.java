package com.tbot.ruler.service;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.configuration.AppliancesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppliancesService {

    @Autowired
    private AppliancesConfiguration appliancesConfiguration;

    public List<Appliance> allAppliances() {
        return appliancesConfiguration.appliances();
    }

    public Appliance applianceById(ApplianceId applianceId) {
        return appliancesConfiguration.appliancesPerId().get(applianceId);
    }
}
