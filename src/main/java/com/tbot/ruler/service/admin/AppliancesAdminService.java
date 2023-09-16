package com.tbot.ruler.service.admin;

import com.tbot.ruler.persistance.AppliancesRepository;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppliancesAdminService {

    @Autowired
    private AppliancesRepository appliancesRepository;

    public List<ApplianceEntity> allAppliances() {
        return appliancesRepository.findAll();
    }

    public ApplianceEntity applianceByUuid(String applianceUuid) {
        return appliancesRepository.findByUuid(applianceUuid).orElse(null);
    }
}
