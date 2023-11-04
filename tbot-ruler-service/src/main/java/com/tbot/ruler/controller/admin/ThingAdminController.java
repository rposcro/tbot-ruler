package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.CreateThingRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.UpdateThingRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
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

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping(path = "/admin/things")
public class ThingAdminController extends AbstractController {

    @Autowired
    private SubjectsAccessor subjectsAccessor;

    @Autowired
    private ThingsRepository thingsRepository;

    @GetMapping
    public ResponseEntity<List<ThingEntity>> getAllThings() {
        return ok(thingsRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ThingResponse> createThing(@RequestBody CreateThingRequest createThingRequest) {
        ThingEntity thingEntity = ThingEntity.builder()
                .thingUuid("thng-" + UUID.randomUUID())
                .name(createThingRequest.getName())
                .description(createThingRequest.getDescription())
                .configuration(createThingRequest.getConfiguration())
                .build();
        thingEntity = thingsRepository.save(thingEntity);
        return ok(toResponse(thingEntity));
    }

    @PatchMapping("/{thingUuid}")
    public ResponseEntity<ThingResponse> updateThing(
            @PathVariable String thingUuid,
            @RequestBody UpdateThingRequest updateThingRequest) {
        ThingEntity thingEntity = subjectsAccessor.findThing(thingUuid);
        thingEntity.setName(updateThingRequest.getName());
        thingEntity.setDescription(updateThingRequest.getDescription());
        thingEntity.setConfiguration(updateThingRequest.getConfiguration());
        thingEntity = thingsRepository.save(thingEntity);
        return ok(toResponse(thingEntity));
    }

    @DeleteMapping("/{thingUuid}")
    public ResponseEntity<ThingResponse> deleteThing(@PathVariable String thingUuid) {
        ThingEntity thingEntity = subjectsAccessor.findThing(thingUuid);
        thingsRepository.delete(thingEntity);
        return ok(toResponse(thingEntity));
    }

    private ThingResponse toResponse(ThingEntity entity) {
        return ThingResponse.builder()
                .thingUuid(entity.getThingUuid())
                .name(entity.getName())
                .description(entity.getDescription())
                .configuration(entity.getConfiguration())
                .build();
    }
}
