package com.tbot.ruler.persistance;

import com.tbot.ruler.exceptions.PersistenceException;
import com.tbot.ruler.persistance.jdbc.CrudSubjectStatesRepository;
import com.tbot.ruler.persistance.model.SubjectStateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectStatesRepository {

    @Autowired
    private CrudSubjectStatesRepository crudSubjectStatesRepository;

    public Optional<SubjectStateEntity> findBySubjectUuid(String subjectUuid) {
        return crudSubjectStatesRepository.findBySubjectUuid(subjectUuid);
    }

    public SubjectStateEntity save(SubjectStateEntity stateEntity) {
        Optional<SubjectStateEntity> optional = crudSubjectStatesRepository.findBySubjectUuid(stateEntity.getSubjectUuid());
        int updatedCnt;
        if (optional.isPresent()) {
            updatedCnt = crudSubjectStatesRepository.update(stateEntity.getSubjectUuid(), stateEntity.getPayload());
        } else {
            updatedCnt = crudSubjectStatesRepository.insert(stateEntity.getSubjectUuid(), stateEntity.getPayload());
        }

        if (updatedCnt != 1) {
            throw new PersistenceException("Subject state save failed, updated count is " + updatedCnt);
        }
        return stateEntity;
    }
}