package com.tbot.ruler.service.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.persistance.SubjectStatesRepository;
import com.tbot.ruler.persistance.model.SubjectStateEntity;
import com.tbot.ruler.subjects.SubjectState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SubjectStatePersistenceService {

    @Autowired
    private SubjectStatesRepository subjectStatesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void persistState(SubjectState subjectState) {
        SubjectStateEntity stateEntity = new SubjectStateEntity(
                subjectState.getSubjectUuid(),
                objectMapper.valueToTree(subjectState.getPayload()));
        subjectStatesRepository.save(stateEntity);
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
}
