package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.PluginEntity;

import java.util.List;

public interface PluginsRepository {

    List<PluginEntity> findAll();
}
