package com.tbot.ruler.controller.subject;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.subject.payload.ActuatorResponse;
import com.tbot.ruler.service.things.ActuatorsService;
import com.tbot.ruler.subjects.Actuator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/actuators")
public class ActuatorsController extends AbstractController {

    @Autowired
    private ActuatorsService actuatorsService;

    @GetMapping(value = "")
    public ResponseEntity<List<ActuatorResponse>> getAll() {
        List<ActuatorResponse> entities = actuatorsService.findAllActuators().stream()
            .map(this::fromActuator)
            .collect(Collectors.toList());
        return ok(entities);
    }

    private ActuatorResponse fromActuator(Actuator actuator) {
        return ActuatorResponse.builder()
                .uuid(actuator.getUuid())
                .name(actuator.getName())
                .description(actuator.getDescription())
                .state(actuator.getState() != null ? actuator.getState().getPayload() : null)
                .build();
    }
}
