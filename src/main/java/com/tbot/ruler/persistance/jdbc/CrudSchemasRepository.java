package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.SchemaEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CrudSchemasRepository extends CrudRepository<SchemaEntity, Long> {

    @Query("SELECT * FROM schemas WHERE schema_uuid = :schemaUuid")
    Optional<SchemaEntity> findByUuid(String schemaUuid);
}
