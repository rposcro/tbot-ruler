package com.tbot.ruler.service.things;

import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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
public class BindingsLifetimeService implements InitializingBean {

    @Autowired
    private BindingsRepository bindingsRepository;

    private Map<String, Set<String>> bindingsMap;

    @Override
    public void afterPropertiesSet() {
        Collectors.mapping(BindingEntity::getReceiverUuid, Collectors.<String>toSet());
        this.bindingsMap = bindingsRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        BindingEntity::getSenderUuid,
                        Collectors.mapping(BindingEntity::getReceiverUuid, Collectors.<String>toSet())));
    }

    public Set<String> getReceiversForSender(String senderUuid) {
        Set<String> receivers = bindingsMap.get(senderUuid);
        return receivers != null ? Collections.unmodifiableSet(receivers) : Collections.emptySet();
    }
}
