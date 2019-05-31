package com.tbot.ruler.service.admin;

import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.things.dto.ThingPluginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginsAdminService {

    @Autowired
    private DTOConfiguration dtoConfiguration;

    public List<ThingPluginDTO> allPlugins() {
        return dtoConfiguration.pluginDTOs();
    }
}
