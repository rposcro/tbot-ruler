package com.tbot.ruler.service.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.persistance.SubjectStatesRepository;
import com.tbot.ruler.persistance.model.SubjectStateEntity;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import com.tbot.ruler.subjects.SubjectState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SubjectStateService {

    @Autowired
    private SubjectStatesRepository subjectStatesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void persistState(ActuatorState actuatorState) {
        SubjectStateEntity entity = new SubjectStateEntity();
        entity.setSubjectUuid(actuatorState.getActuatorUuid());
        entity.setPayload(objectMapper.valueToTree(actuatorState.getPayload()));
        subjectStatesRepository.save(entity);
    }

    public <T> SubjectState<T> recoverState(String subjectUuid, Class<T> payloadClass) {
        try {
            SubjectStateEntity stateEntity = subjectStatesRepository.findBySubjectUuid(subjectUuid).orElse(null);
            T payload = objectMapper.readerFor(payloadClass).readValue(stateEntity.getPayload());
            return SubjectState.<T>builder()
                    .subjectUuid(subjectUuid)
                    .payload(payload)
                    .build();
        } catch(IOException e) {
            log.warn("Failed to recover state for subject {}", subjectUuid);
            return null;
        }
    }

    public <T> ActuatorState<T> recoverActuatorState(String subjectUuid, Class<T> payloadClass) {
        try {
            SubjectStateEntity stateEntity = subjectStatesRepository.findBySubjectUuid(subjectUuid).orElse(null);
            if (stateEntity != null) {
                T payload = objectMapper.readerFor(payloadClass).readValue(stateEntity.getPayload());
                return ActuatorState.<T>builder()
                        .actuatorUuid(subjectUuid)
                        .payload(payload)
                        .build();
            } else {
                return null;
            }
        } catch(IOException e) {
            log.warn("Failed to recover state for actuator {}", subjectUuid);
            return null;
        }
    }
}
