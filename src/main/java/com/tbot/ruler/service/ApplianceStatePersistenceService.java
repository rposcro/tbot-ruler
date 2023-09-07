package com.tbot.ruler.service;

import com.tbot.ruler.persistance.ApplianceState;
import com.tbot.ruler.persistance.ApplianceStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ApplianceStatePersistenceService {

    @Autowired
    private ApplianceStateRepository applianceStateRepository;

    public void persist(String itemId, Object value) {
        applianceStateRepository.save(ApplianceState.builder()
                .key(itemId)
                .valueClass(value.getClass())
                .value(value)
                .build());
    }

    public <T> Optional<T> retrieve(String itemId) {
        return applianceStateRepository.findByKey(itemId).map(value -> (T) value.getValue());
    }
}
