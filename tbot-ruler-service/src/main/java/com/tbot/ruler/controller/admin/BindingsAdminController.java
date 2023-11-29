package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.BindingCreateRequest;
import com.tbot.ruler.controller.admin.payload.BindingDeleteRequest;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.service.manipulators.BindingsManipulator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/bindings")
public class BindingsAdminController extends AbstractController {

    @Autowired
    private RepositoryAccessor repositoryAccessor;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private BindingsManipulator bindingsManipulator;

    @GetMapping
    public ResponseEntity<List<BindingResponse>> getAllBindings() {
        return ok(bindingsRepository.findAll().stream().map(this::toResponse).toList());
    }

    @GetMapping("/senders/{senderUuid}")
    public ResponseEntity<List<BindingResponse>> getSenderBindings(@PathVariable String senderUuid) {
        return ok(bindingsRepository.findBySenderUuid(senderUuid).stream().map(this::toResponse).toList());
    }

    @GetMapping("/receivers/{receiverUuid}")
    public ResponseEntity<List<BindingResponse>> getReceiverBindings(@PathVariable String receiverUuid) {
        return ok(bindingsRepository.findByReceiverUuid(receiverUuid).stream().map(this::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<BindingResponse> createBinding(@RequestBody BindingCreateRequest bindingCreateRequest) {
        bindingsManipulator.addBinding(bindingCreateRequest.getSenderUuid(), bindingCreateRequest.getReceiverUuid());
        return ok(toResponse(repositoryAccessor.findBinding(
                bindingCreateRequest.getSenderUuid(), bindingCreateRequest.getReceiverUuid())));
    }

    @DeleteMapping
    public ResponseEntity<BindingResponse> deleteBinding(@RequestBody BindingDeleteRequest bindingDeleteRequest) {
        BindingEntity bindingEntity = repositoryAccessor.findBinding(
                bindingDeleteRequest.getSenderUuid(), bindingDeleteRequest.getReceiverUuid());
        bindingsManipulator.removeBinding(bindingEntity);
        return ok(toResponse(bindingEntity));
    }

    private BindingResponse toResponse(BindingEntity entity) {
        return BindingResponse.builder()
                .senderUuid(entity.getSenderUuid())
                .receiverUuid(entity.getReceiverUuid())
                .build();
    }
}
