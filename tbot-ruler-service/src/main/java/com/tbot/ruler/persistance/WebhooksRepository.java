package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.jdbc.CrudWebhooksRepository;
import com.tbot.ruler.persistance.model.WebhookEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WebhooksRepository extends AbstractRepository<WebhookEntity> {

    @Autowired
    private CrudWebhooksRepository crudWebhooksRepository;

    public List<WebhookEntity> findAll() {
        List<WebhookEntity> entities = StreamSupport.stream(crudWebhooksRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return entities;
    }

    public Optional<WebhookEntity> findById(long webhookId) {
        Optional<WebhookEntity> entity = crudWebhooksRepository.findById(webhookId);
        return entity;
    }

    public Optional<WebhookEntity> findByUuid(String webhookUuid) {
        Optional<WebhookEntity> entity = crudWebhooksRepository.findByUuid(webhookUuid);
        return entity;
    }

    @Transactional
    public void delete(WebhookEntity webhookEntity) {
        crudWebhooksRepository.delete(webhookEntity);
        triggerDeleted(webhookEntity);
    }

    @Transactional
    public WebhookEntity save(WebhookEntity webhookEntity) {
        WebhookEntity savedWebhookEntity = crudWebhooksRepository.save(webhookEntity);

        if (webhookEntity.getWebhookId() == 0) {
            triggerInserted(savedWebhookEntity);
        } else {
            triggerUpdated(savedWebhookEntity);
        }

        return savedWebhookEntity;
    }
}
