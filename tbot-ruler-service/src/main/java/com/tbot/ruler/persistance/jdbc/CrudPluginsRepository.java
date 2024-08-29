package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.PluginEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.StreamSupport;

public interface CrudPluginsRepository extends CrudRepository<PluginEntity, Long> {

    @Query("SELECT * FROM plugins WHERE plugin_uuid = :pluginUuid")
    Optional<PluginEntity> findByUuid(String pluginUuid);

    StreamSupport.strm
}
