package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.ActuatorCreateRequest;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActuatorsClient extends AbstractApiClient {

    @Autowired
    private ActuatorsAdminApi actuatorsAdminApi;

    public List<ActuatorResponse> getAllActuators() {
        return executeApiFunction(() -> actuatorsAdminApi.getActuators().execute());
    }

    public void updateActuator(String actuatorUuid, ActuatorUpdateRequest updateRequest) {
        executeApiFunction(() -> actuatorsAdminApi.updateActuator(actuatorUuid, updateRequest).execute());
    }

    public void createActuator(ActuatorCreateRequest createRequest) {
        executeApiFunction(() -> actuatorsAdminApi.createActuator(createRequest).execute());
    }

    public void deleteActuator(String actuatorUuid) {
        executeApiFunction(() -> actuatorsAdminApi.deleteActuator(actuatorUuid).execute());
    }
}
