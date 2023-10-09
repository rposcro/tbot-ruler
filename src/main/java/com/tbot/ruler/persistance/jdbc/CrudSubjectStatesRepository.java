package com.tbot.ruler.persistance.jdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.persistance.model.SubjectStateEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CrudSubjectStatesRepository extends Repository<SubjectStateEntity, String> {

    @Query("SELECT * FROM subject_states WHERE subject_uuid = :subjectUuid")
    Optional<SubjectStateEntity> findBySubjectUuid(String subjectUuid);

    @Modifying
    @Query("DELETE FROM subject_states WHERE subject_uuid = :#{#entity.subjectUuid}")
    void delete(SubjectStateEntity entity);

    @Modifying
    @Query("INSERT INTO subject_states (subject_uuid, payload) VALUES (:#{#entity.subjectUuid}, :#{#entity.payload})")
    int insert(SubjectStateEntity entity);

    @Modifying
    @Query("INSERT INTO subject_states (subject_uuid, payload) VALUES (:subjectUuid, :payload)")
    int insert(String subjectUuid, JsonNode payload);

    @Modifying
    @Query("UPDATE subject_states SET payload = :#{#entity.payload) WHERE subject_uuid = :#{#entity.subjectUuid}")
    int update(SubjectStateEntity entity);

    @Modifying
    @Query("UPDATE subject_states SET payload = :payload WHERE subject_uuid = :subjectUuid")
    int update(String subjectUuid, JsonNode payload);
}
