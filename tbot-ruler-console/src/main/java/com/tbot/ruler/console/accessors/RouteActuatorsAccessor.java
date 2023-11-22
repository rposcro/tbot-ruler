package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.ActuatorsClient;
import com.tbot.ruler.controller.admin.payload.ActuatorCreateRequest;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RouteScope
@SpringComponent
public class RouteActuatorsAccessor {

    @Autowired
    private ActuatorsClient actuatorsClient;

    public List<ActuatorResponse> getAllActuators() {
        return actuatorsClient.getAllActuators();
    }

    public Map<String, ActuatorResponse> getActuatorsUuidMap() {
        return getAllActuators().stream()
                .collect(Collectors.toMap(ActuatorResponse::getActuatorUuid, Function.identity()));
    }

    public void updateActuator(String actuatorUuid, ActuatorUpdateRequest updateRequest) {
        actuatorsClient.updateActuator(actuatorUuid, updateRequest);
    }

    public void createActuator(ActuatorCreateRequest createRequest) {
        actuatorsClient.createActuator(createRequest);
    }
}
