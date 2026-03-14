package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudPluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PluginsRepository {

    @Autowired
    private CrudPluginsRepository crudPluginsRepository;

    public List<PluginEntity> findAll() {
        List<PluginEntity> entities = StreamSupport.stream(crudPluginsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return entities;
    }

    public Optional<PluginEntity> findById(long pluginId) {
        return crudPluginsRepository.findById(pluginId);
    }

    public Optional<PluginEntity> findByUuid(String pluginUuid) {
        return crudPluginsRepository.findByUuid(pluginUuid);
    }

    @Transactional
    public void delete(PluginEntity pluginEntity) {
        crudPluginsRepository.delete(pluginEntity);
    }

    @Transactional
    public void deleteAll() {
        crudPluginsRepository.deleteAll();
    }

    @Transactional
    public PluginEntity save(PluginEntity pluginEntity) {
        PluginEntity savedPlugin = crudPluginsRepository.save(pluginEntity);
        return savedPlugin;
    }
}
