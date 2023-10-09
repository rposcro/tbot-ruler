package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.CreateActuatorRequest;
import com.tbot.ruler.controller.admin.payload.UpdateActuatorRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping(path = "/admin/actuators")
public class ActuatorAdminController extends AbstractController {

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @PostMapping
    public ResponseEntity<ActuatorEntity> createActuator(@RequestBody CreateActuatorRequest createActuatorRequest) {
        ThingEntity thingEntity = findThing(createActuatorRequest.getThingUuid());
        ActuatorEntity actuatorEntity = ActuatorEntity.builder()
                .actuatorUuid("actr-" + UUID.randomUUID())
                .name(createActuatorRequest.getName())
                .description(createActuatorRequest.getDescription())
                .configuration(createActuatorRequest.getConfiguration())
                .thingId(thingEntity.getThingId())
                .build();
        actuatorsRepository.save(actuatorEntity);
        return ok(actuatorEntity);
    }

    @PatchMapping("/{actuatorUuid}")
    public ResponseEntity<ActuatorEntity> updateActuator(
            @PathVariable String actuatorUuid,
            @RequestBody UpdateActuatorRequest updateActuatorRequest) {
        ActuatorEntity actuatorEntity = findActuator(actuatorUuid);
        actuatorEntity.setName(updateActuatorRequest.getName());
        actuatorEntity.setDescription(updateActuatorRequest.getDescription());
        actuatorEntity.setConfiguration(updateActuatorRequest.getConfiguration());
        actuatorsRepository.save(actuatorEntity);
        return ok(actuatorEntity);
    }

    @DeleteMapping("/{actuatorUuid}")
    public ResponseEntity<ActuatorEntity> deleteActuator(@PathVariable String actuatorUuid) {
        ActuatorEntity actuatorEntity = findActuator(actuatorUuid);
        actuatorsRepository.delete(actuatorEntity);
        return ok(actuatorEntity);
    }

    private ActuatorEntity findActuator(String actuatorUuid) {
        return actuatorsRepository.findByUuid(actuatorUuid)
                .orElseThrow(() -> new ServiceRequestException(format("Actuator %s not found!", actuatorUuid)));
    }

    private ThingEntity findThing(String thingUuid) {
        return thingsRepository.findByUuid(thingUuid)
                .orElseThrow(() -> new ServiceRequestException(format("Thing %s not found!", thingUuid)));
    }
}
