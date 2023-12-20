package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope("singleton")
public class BindingsLifecycleService {

    @Autowired
    private BindingsRepository bindingsRepository;

    private Map<String, Set<String>> sendersToReceiversMap;

    public Set<String> getReceiversForSender(String senderUuid) {
        Set<String> receivers = sendersToReceiversMap.get(senderUuid);
        return receivers != null ? Collections.unmodifiableSet(receivers) : Collections.emptySet();
    }

    public void reloadCache() {
        this.sendersToReceiversMap = bindingsRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        BindingEntity::getSenderUuid,
                        Collectors.mapping(BindingEntity::getReceiverUuid, Collectors.<String>toSet())));
    }
}
