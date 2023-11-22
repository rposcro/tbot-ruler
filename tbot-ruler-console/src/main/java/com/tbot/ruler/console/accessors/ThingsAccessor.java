package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.ThingsClient;
import com.tbot.ruler.controller.admin.payload.ThingCreateRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.ThingUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RouteScope
@SpringComponent
public class ThingsAccessor {

    @Autowired
    private ThingsClient thingsClient;

    public List<ThingResponse> getAllThings() {
        return thingsClient.getAllThings();
    }

    public Map<String, ThingResponse> getThingsUuidMap() {
        return getAllThings().stream()
                .collect(Collectors.toMap(ThingResponse::getThingUuid, Function.identity()));
    }

    public void updateThing(String thingUuid, ThingUpdateRequest updateRequest) {
        thingsClient.updateThing(thingUuid, updateRequest);
    }

    public void createThing(ThingCreateRequest createRequest) {
        thingsClient.createThing(createRequest);
    }
}
