package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.StencilsClient;
import com.tbot.ruler.controller.admin.payload.StencilCreateRequest;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.tbot.ruler.controller.admin.payload.StencilUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RouteScope
@SpringComponent
public class StencilsAccessor {

    @Autowired
    private StencilsClient stencilsClient;

    public List<StencilResponse> getAllStencils() {
        return stencilsClient.getAllStencils();
    }

    public Map<String, StencilResponse> getStencilsUuidMap() {
        return getAllStencils().stream()
                .collect(Collectors.toMap(StencilResponse::getStencilUuid, Function.identity()));
    }

    public void updateStencil(String stencilUuid, StencilUpdateRequest updateRequest) {
        stencilsClient.updateStencil(stencilUuid, updateRequest);
    }

    public void createStencil(StencilCreateRequest createRequest) {
        stencilsClient.createStencil(createRequest);
    }
}
