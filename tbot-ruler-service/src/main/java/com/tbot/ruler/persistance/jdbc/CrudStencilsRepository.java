package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.StencilEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CrudStencilsRepository extends CrudRepository<StencilEntity, Long> {

    @Query("SELECT * FROM stencils WHERE stencil_uuid = :stencilUuid")
    Optional<StencilEntity> findByUuid(String stencilUuid);

    @Query("SELECT * FROM stencils WHERE owner = :owner AND type = :type")
    Optional<StencilEntity> findByOwnerAndType(String owner, String type);
}
