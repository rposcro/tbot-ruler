package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.SubjectStateEntity;

import java.util.Optional;

public interface SubjectStatesRepository {

    Optional<SubjectStateEntity> findBySubjectUuid(String subjectUuid);

    SubjectStateEntity save(SubjectStateEntity stateEntity);
}