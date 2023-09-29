package com.tbot.ruler.service.admin;

import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginsAdminService {

    @Autowired
    private PluginsRepository pluginsRepository;

    public List<PluginEntity> allPlugins() {
        return pluginsRepository.findAll();
    }
}
