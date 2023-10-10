package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudBindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BindingsRepository {

    @Autowired
    private CrudBindingsRepository crudBindingsRepository;

    public List<BindingEntity> findAll() {
        return StreamSupport.stream(crudBindingsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<BindingEntity> findBySenderUuid(String senderUuid) {
        return StreamSupport.stream(crudBindingsRepository.findBySenderUuid(senderUuid).spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<BindingEntity> findByReceiverUuid(String receiverUuid) {
        return StreamSupport.stream(crudBindingsRepository.findByReceiverUuid(receiverUuid).spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean bindingExists(String senderUuid, String receiverUuid) {
        return crudBindingsRepository.bindingExists(senderUuid, receiverUuid);
    }

    public void delete(BindingEntity bindingEntity) {
        crudBindingsRepository.delete(bindingEntity);
    }

    public void insert(BindingEntity bindingEntity) {
        crudBindingsRepository.save(bindingEntity);
    }
}
