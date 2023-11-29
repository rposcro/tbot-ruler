package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.service.lifecycle.BindingsLifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BindingsManipulator {

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private BindingsLifecycleService bindingsLifecycleService;

    public void addBinding(String senderUuid, String receiverUuid) {
        if (bindingsRepository.bindingExists(senderUuid, receiverUuid)) {
            throw new LifecycleException("Binding of %s to %s already exists!", senderUuid, receiverUuid);
        }

        if (!bindingsRepository.insert(BindingEntity.builder()
                        .senderUuid(senderUuid)
                        .receiverUuid(receiverUuid)
                        .build())) {
            throw new LifecycleException("Failed to add binding of %s to %s!", senderUuid, receiverUuid);
        }

        bindingsLifecycleService.reloadCache();
    }

    public void removeBinding(BindingEntity bindingEntity) {
        bindingsRepository.delete(bindingEntity);
        bindingsLifecycleService.reloadCache();
    }
}
