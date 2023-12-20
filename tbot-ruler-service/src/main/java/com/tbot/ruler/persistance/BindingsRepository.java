package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudBindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BindingsRepository extends AbstractRepository<BindingEntity> {

    @Autowired
    private CrudBindingsRepository crudBindingsRepository;

    public List<BindingEntity> findAll() {
        return StreamSupport.stream(crudBindingsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<BindingEntity> find(String senderUuid, String receiverUuid) {
        return crudBindingsRepository.find(senderUuid, receiverUuid);
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

    public boolean bindingWithUuidExists(String uuid) {
        return crudBindingsRepository.bindingExists(uuid);
    }

    public void delete(BindingEntity bindingEntity) {
        crudBindingsRepository.delete(bindingEntity);
        triggerDeleted(bindingEntity);
    }

    public boolean insert(BindingEntity bindingEntity) {
        if (crudBindingsRepository.save(bindingEntity) == 1) {
            triggerInserted(bindingEntity);
            return true;
        }
        return false;
    }
}
