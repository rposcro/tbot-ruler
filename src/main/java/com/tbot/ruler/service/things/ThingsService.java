package com.tbot.ruler.service.things;

import com.tbot.ruler.configuration.ActuatorsConfiguration;
import com.tbot.ruler.configuration.CollectorsConfiguration;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.ItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingsService {

    @Autowired
    private CollectorsConfiguration collectorsConfiguration;

    @Autowired
    private ActuatorsConfiguration actuatorsConfiguration;

    public Collector collectorById(ItemId collectorId) {
        return collectorsConfiguration.collectorsPerId().get(collectorId);
    }

    public Actuator actuatorById(ItemId actuatorId) {
        return actuatorsConfiguration.actuatorsPerId().get(actuatorId);
    }
}
