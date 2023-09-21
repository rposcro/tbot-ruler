package com.tbot.ruler.service.things;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.subjects.Actuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ThingsService {

    @Autowired
    private PluginsRepository pluginsRepository;

    private Map<String, Actuator> actuatorsMap;

    @PostConstruct
    public void init() {
        this.actuatorsMap = new HashMap<>();
        pluginsRepository.findAll();
    }
}
