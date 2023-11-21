package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.ThingCreateRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.ThingUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ThingsClient extends AbstractApiClient {

    @Autowired
    private ThingsAdminApi thingsAdminApi;

    public List<ThingResponse> getAllThings() {
        return executeApiFunction(() -> thingsAdminApi.getThings().execute());
    }

    public void updateThing(String thingUuid, ThingUpdateRequest updateRequest) {
        executeApiFunction(() -> thingsAdminApi.updateThing(thingUuid, updateRequest).execute());
    }

    public void createThing(ThingCreateRequest createRequest) {
        executeApiFunction(() -> thingsAdminApi.createThing(createRequest).execute());
    }
}
