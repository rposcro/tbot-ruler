package com.tbot.ruler.persistance.jdbc;

import com.tbot.ruler.persistance.model.WebhookEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CrudWebhooksRepository extends CrudRepository<WebhookEntity, Long> {

    @Query("SELECT * FROM webhooks WHERE webhook_uuid = :webhookUuid")
    Optional<WebhookEntity> findByUuid(String webhookUuid);
}
