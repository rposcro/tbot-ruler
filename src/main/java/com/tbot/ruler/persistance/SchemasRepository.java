package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudSchemasRepository;
import com.tbot.ruler.persistance.model.SchemaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SchemasRepository {

    @Autowired
    private CrudSchemasRepository crudSchemasRepository;

    public Optional<SchemaEntity> findByUuid(String schemaUuid) {
        return crudSchemasRepository.findByUuid(schemaUuid);
    }

    public Optional<SchemaEntity> findByOwnerAndType(String owner, String type) {
        return crudSchemasRepository.findByOwnerAndType(owner, type);
    }

    @Transactional
    public SchemaEntity save(SchemaEntity schemaEntity) {
        return crudSchemasRepository.save(schemaEntity);
    }
}
