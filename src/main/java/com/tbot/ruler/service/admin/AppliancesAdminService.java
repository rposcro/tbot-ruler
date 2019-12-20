package com.tbot.ruler.service.admin;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppliancesAdminService {

    @Autowired
    private DTOConfiguration dtoConfiguration;

    public List<ApplianceDTO> allAppliances() {
        return dtoConfiguration.applianceDTOs();
    }

    public ApplianceDTO applianceDTOById(ApplianceId applianceId) {
        return dtoConfiguration.applianceDTOMap().get(applianceId);
    }
}
