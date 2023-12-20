package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import com.tbot.ruler.persistance.WebhooksRepository;
import com.tbot.ruler.persistance.model.WebhookEntity;
import com.tbot.ruler.service.manipulators.WebhooksManipulator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/admin/webhooks")
public class WebhooksAdminController extends AbstractController {

    @Autowired
    private RepositoryAccessor subjectsAccessor;

    @Autowired
    private WebhooksRepository webhooksRepository;

    @Autowired
    private WebhooksManipulator webhooksManipulator;

    @GetMapping("/owners")
    public ResponseEntity<List<String>> getFactories() {
        return ok(List.of("tbot-panel"));
    }

    @GetMapping
    public ResponseEntity<List<WebhookResponse>> getAllWebhooks() {
        return ok(webhooksRepository.findAll().stream().map(this::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<WebhookResponse> createWebhook(@RequestBody WebhookCreateRequest webhookCreateRequest) {
        WebhookEntity webhookEntity = WebhookEntity.builder()
                .webhookUuid("wbhk-" + UUID.randomUUID())
                .name(webhookCreateRequest.getName())
                .owner(webhookCreateRequest.getOwner())
                .description(webhookCreateRequest.getDescription())
                .build();
        webhookEntity = webhooksRepository.save(webhookEntity);
        return ok(toResponse(webhookEntity));
    }

    @PatchMapping("/{webhookUuid}")
    public ResponseEntity<WebhookResponse> updateWebhook(
            @PathVariable String webhookUuid,
            @RequestBody WebhookUpdateRequest webhookUpdateRequest) {
        WebhookEntity webhookEntity = subjectsAccessor.findWebhook(webhookUuid);
        webhookEntity.setName(webhookUpdateRequest.getName());
        webhookEntity.setDescription(webhookUpdateRequest.getDescription());
        webhookEntity = webhooksRepository.save(webhookEntity);
        return ok(toResponse(webhookEntity));
    }

    @DeleteMapping("/{webhookUuid}")
    public ResponseEntity<WebhookResponse> deleteWebhook(@PathVariable String webhookUuid) {
        WebhookEntity webhookEntity = subjectsAccessor.findWebhook(webhookUuid);
        webhooksManipulator.removeWebhook(webhookEntity);
        return ok(toResponse(webhookEntity));
    }

    private WebhookResponse toResponse(WebhookEntity entity) {
        return WebhookResponse.builder()
                .webhookUuid(entity.getWebhookUuid())
                .owner(entity.getOwner())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
