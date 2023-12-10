package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudStencilsRepository;
import com.tbot.ruler.persistance.model.StencilEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StencilsRepository {

    @Autowired
    private CrudStencilsRepository crudStencilsRepository;

    public List<StencilEntity> findAll() {
        return StreamSupport.stream(crudStencilsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<StencilEntity> findByUuid(String stencilUuid) {
        return crudStencilsRepository.findByUuid(stencilUuid);
    }

    public Optional<StencilEntity> findByOwnerAndType(String owner, String type) {
        return crudStencilsRepository.findByOwnerAndType(owner, type);
    }

    @Transactional
    public StencilEntity save(StencilEntity stencilEntity) {
        return crudStencilsRepository.save(stencilEntity);
    }
}
