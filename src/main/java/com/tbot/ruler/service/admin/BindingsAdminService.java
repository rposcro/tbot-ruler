package com.tbot.ruler.service.admin;

import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BindingsAdminService {

    @Autowired
    private BindingsRepository bindingsRepository;

    public List<BindingEntity> allBindings() {
        return bindingsRepository.findAll();
    }

    public List<String> sendersForReceiver(String receiverUuid) {
        return bindingsRepository.findAll().stream()
                .filter(binding -> binding.getReceiverUuid().equals(receiverUuid))
                .map(BindingEntity::getSenderUuid)
                .collect(Collectors.toList());
    }

    public List<String> receiversForSender(String senderUuid) {
        return bindingsRepository.findAll().stream()
                .filter(binding -> binding.getSenderUuid().equals(senderUuid))
                .map(BindingEntity::getReceiverUuid)
                .collect(Collectors.toList());
    }
}
