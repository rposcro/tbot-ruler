package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.ActuatorCreateRequest;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.lifecycle.ActuatorsLifecycleService;
import com.tbot.ruler.service.manipulators.ActuatorsManipulator;
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
@RequestMapping(path = "/admin/actuators")
public class ActuatorsAdminController extends AbstractController {

    @Autowired
    private RepositoryAccessor subjectsAccessor;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private ActuatorsManipulator actuatorsManipulator;

    @Autowired
    private ActuatorsLifecycleService actuatorsLifecycleService;

    @GetMapping
    public ResponseEntity<List<ActuatorResponse>> getAllActuators() {
        return ok(actuatorsRepository.findAll().stream()
                .map(this::toResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<ActuatorResponse> createActuator(@RequestBody ActuatorCreateRequest actuatorCreateRequest) {
        PluginEntity pluginEntity = subjectsAccessor.findPlugin(actuatorCreateRequest.getPluginUuid());
        ThingEntity thingEntity = subjectsAccessor.findThing(actuatorCreateRequest.getThingUuid());
        ActuatorEntity actuatorEntity = ActuatorEntity.builder()
                .actuatorUuid("actr-" + UUID.randomUUID())
                .name(actuatorCreateRequest.getName())
                .reference(actuatorCreateRequest.getReference())
                .description(actuatorCreateRequest.getDescription())
                .configuration(actuatorCreateRequest.getConfiguration())
                .pluginId(pluginEntity.getPluginId())
                .thingId(thingEntity.getThingId())
                .build();
        actuatorEntity = actuatorsManipulator.createActuator(actuatorEntity);
        return ok(toResponse(actuatorEntity));
    }

    @PatchMapping("/{actuatorUuid}")
    public ResponseEntity<ActuatorResponse> updateActuator(
            @PathVariable String actuatorUuid,
            @RequestBody ActuatorUpdateRequest actuatorUpdateRequest) {
        ActuatorEntity actuatorEntity = subjectsAccessor.findActuator(actuatorUuid);
        ThingEntity thingEntity = subjectsAccessor.findThing(actuatorUpdateRequest.getThingUuid());
        actuatorEntity.setName(actuatorUpdateRequest.getName());
        actuatorEntity.setDescription(actuatorUpdateRequest.getDescription());
        actuatorEntity.setConfiguration(actuatorUpdateRequest.getConfiguration());
        actuatorEntity.setThingId(thingEntity.getThingId());
        actuatorEntity = actuatorsManipulator.updateActuator(actuatorEntity);
        return ok(toResponse(actuatorEntity));
    }

    @DeleteMapping("/{actuatorUuid}")
    public ResponseEntity<ActuatorResponse> deleteActuator(@PathVariable String actuatorUuid) {
        ActuatorEntity actuatorEntity = subjectsAccessor.findActuator(actuatorUuid);
        actuatorsManipulator.removeActuator(actuatorEntity);
        return ok(toResponse(actuatorEntity));
    }

    private ActuatorResponse toResponse(ActuatorEntity actuatorEntity) {
        return ActuatorResponse.builder()
                .actuatorUuid(actuatorEntity.getActuatorUuid())
                .thingUuid(subjectsAccessor.findThing(actuatorEntity.getThingId()).getThingUuid())
                .pluginUuid(subjectsAccessor.findPlugin(actuatorEntity.getPluginId()).getPluginUuid())
                .name(actuatorEntity.getName())
                .reference(actuatorEntity.getReference())
                .description(actuatorEntity.getDescription())
                .configuration(actuatorEntity.getConfiguration())
                .relaunchRequired(actuatorsLifecycleService.isActuatorStale(actuatorEntity.getActuatorUuid()))
                .build();
    }
}
