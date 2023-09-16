package com.tbot.ruler.service.admin;

import com.tbot.ruler.configuration.DTOConfiguration;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import com.tbot.ruler.things.builder.dto.ItemDTO;
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

    @Autowired
    private DTOConfiguration dtoConfiguration;

    public List<BindingEntity> allBindings() {
        return bindingsRepository.findAll();
    }

    public List<ItemDTO> sendersForItem(String itemId) {
        List<ItemDTO> senders = dtoConfiguration.bindingDTOs().stream()
                .filter(binding -> binding.getConsumerIds().contains(itemId))
                .map(BindingDTO::getSenderId)
                .map(this::findSenderDTO)
                .collect(Collectors.toList());
        return senders;
    }

    public List<ItemDTO> receiversForItem(String itemId) {
        List<ItemDTO> listeners = dtoConfiguration.bindingDTOs().stream()
                .filter(binding -> binding.getSenderId().equals(itemId))
                .flatMap(binding -> binding.getConsumerIds().stream())
                .map(this::findListenerDTO)
                .collect(Collectors.toList());
        return listeners;
    }

    private ItemDTO findSenderDTO(String itemId) {
        ItemDTO itemDTO = dtoConfiguration.actuatorDTOMap().get(itemId);
        if (itemDTO == null) {
            itemDTO = dtoConfiguration.applianceDTOMap().get(itemId);
        }
        return itemDTO;
    }

    private ItemDTO findListenerDTO(String itemId) {
        ItemDTO itemDTO = dtoConfiguration.actuatorDTOMap().get(itemId);
        if (itemDTO == null) {
            itemDTO = dtoConfiguration.applianceDTOMap().get(itemId);
        }
        return itemDTO;
    }
}
