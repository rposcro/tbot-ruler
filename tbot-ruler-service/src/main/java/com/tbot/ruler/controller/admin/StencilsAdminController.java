package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.StencilCreateRequest;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.tbot.ruler.controller.admin.payload.StencilUpdateRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.StencilsRepository;
import com.tbot.ruler.persistance.model.StencilEntity;
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
import java.util.stream.Collectors;

import static java.lang.String.format;

@RestController
@RequestMapping("/admin/stencils")
public class StencilsAdminController extends AbstractController {

    @Autowired
    private StencilsRepository stencilsRepository;

    @Autowired
    private RepositoryAccessor repositoryAccessor;

    @GetMapping
    public List<StencilResponse> getAllStencils() {
        List<StencilEntity> stencils = stencilsRepository.findAll();
        return stencils.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/owners/{owner}/types/{type}")
    public ResponseEntity<StencilResponse> getStencil(@PathVariable("owner") String owner, @PathVariable("type") String type) {
        StencilEntity stencilEntity = stencilsRepository.findByOwnerAndType(owner, type)
            .orElseThrow(() -> new ServiceRequestException(format("Stencil %s-%s not found!", owner, type)));
        return ok(toResponse(stencilEntity));
    }

    @PostMapping
    public ResponseEntity<StencilResponse> createStencil(
            @RequestBody StencilCreateRequest stencilCreateRequest) {
        StencilEntity stencilEntity = StencilEntity.builder()
                .stencilUuid("stcl-" + UUID.randomUUID())
                .owner(stencilCreateRequest.getOwner())
                .type(stencilCreateRequest.getType())
                .payload(stencilCreateRequest.getPayload())
                .build();
        stencilEntity = stencilsRepository.save(stencilEntity);
        return ok(toResponse(stencilEntity));
    }

    @PatchMapping("/{stencilUuid}")
    public ResponseEntity<StencilResponse> updateStencil(
            @PathVariable String stencilUuid,
            @RequestBody StencilUpdateRequest stencilUpdateRequest) {
        StencilEntity stencilEntity = repositoryAccessor.findStencil(stencilUuid);
        stencilEntity.setOwner(stencilUpdateRequest.getOwner());
        stencilEntity.setType(stencilUpdateRequest.getType());
        stencilEntity.setPayload(stencilUpdateRequest.getPayload());
        stencilEntity = stencilsRepository.save(stencilEntity);
        return ok(toResponse(stencilEntity));
    }

    @DeleteMapping("/{stencilUuid}")
    public ResponseEntity<StencilResponse> deleteStencil(@PathVariable String stencilUuid) {
        StencilEntity stencilEntity = repositoryAccessor.findStencil(stencilUuid);
        stencilsRepository.delete(stencilEntity);
        return ok(toResponse(stencilEntity));
    }

    private StencilResponse toResponse(StencilEntity entity) {
        return StencilResponse.builder()
                .stencilUuid(entity.getStencilUuid())
                .owner(entity.getOwner())
                .type(entity.getType())
                .payload(entity.getPayload())
                .build();
    }
}
