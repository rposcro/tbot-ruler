package com.tbot.ruler.it;

import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BindingsHelper {

    @Autowired
    private BindingsRepository bindingsRepository;

    public BindingEntity insertBinding(String senderUuid, String receiverUuid) {
        BindingEntity bindingEntity = BindingEntity.builder()
                .senderUuid(senderUuid)
                .receiverUuid(receiverUuid)
                .build();
        bindingsRepository.insert(bindingEntity);
        return bindingEntity;
    }

    public BindingEntity insertSenderBinding(String senderUuid) {
        return insertBinding(senderUuid, "rcv-" + UUID.randomUUID());
    }

}
