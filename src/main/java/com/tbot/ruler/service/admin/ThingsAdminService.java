package com.tbot.ruler.service.admin;

import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.things.ThingId;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThingsAdminService {

    @Autowired
    private DTOConfiguration dtoConfiguration;

    private Map<ThingId, ThingDTO> thingsDTOById;

    public List<ThingDTO> allThings() {
        return dtoConfiguration.thingDTOs();
    }

    public ThingDTO thingDTOById(ThingId thingId) {
        return dtoConfiguration.thingDTOMap().get(thingId);
    }

    public Map<String, List<ThingDTO>> thingsByPlugin() {
        return dtoConfiguration.thingDTOs().stream()
            .collect(Collectors.groupingBy(ThingDTO::getPluginAlias));
    }

}
