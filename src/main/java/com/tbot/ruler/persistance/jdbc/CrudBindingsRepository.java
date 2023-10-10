package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.BindingEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

public interface CrudBindingsRepository extends Repository<BindingEntity, Void> {

    @Query("SELECT * FROM bindings")
    Iterable<BindingEntity> findAll();

    @Query("SELECT * FROM bindings WHERE sender_uuid = :senderUuid")
    Iterable<BindingEntity> findBySenderUuid(String senderUuid);

    @Query("SELECT * FROM bindings WHERE receiver_uuid = :receiverUuid")
    Iterable<BindingEntity> findByReceiverUuid(String receiverUuid);

    @Query("SELECT COUNT(*) = 1 FROM bindings WHERE sender_uuid = :senderUuid AND receiver_uuid = :receiverUuid")
    boolean bindingExists(String senderUuid, String receiverUuid);

    @Modifying
    @Query("DELETE FROM bindings WHERE sender_uuid = :#{#entity.senderUuid} AND receiver_uuid = :#{#entity.receiverUuid}")
    void delete(BindingEntity entity);

    @Modifying
    @Query("INSERT INTO bindings (sender_uuid, receiver_uuid) VALUES (:#{#entity.senderUuid}, :#{#entity.receiverUuid})")
    int save(BindingEntity entity);
}
