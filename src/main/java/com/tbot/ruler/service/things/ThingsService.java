package com.tbot.ruler.service.things;

import com.tbot.ruler.configuration.PluginsConfiguration;
import com.tbot.ruler.things.Actuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThingsService {

    @Autowired
    private PluginsConfiguration pluginsConfiguration;

    public Actuator actuatorById(String actuatorId) {
        return pluginsConfiguration.actuatorsPerId().get(actuatorId);
    }
}
