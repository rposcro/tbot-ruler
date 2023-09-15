package com.tbot.ruler.service.things;

import com.tbot.ruler.configuration.ActuatorsConfiguration;
import com.tbot.ruler.things.Actuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingsService {

    @Autowired
    private ActuatorsConfiguration actuatorsConfiguration;

    public Actuator actuatorById(String actuatorId) {
        return actuatorsConfiguration.actuatorsPerId().get(actuatorId);
    }
}
