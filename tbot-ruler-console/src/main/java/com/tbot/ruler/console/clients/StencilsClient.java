package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.StencilCreateRequest;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.tbot.ruler.controller.admin.payload.StencilUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StencilsClient extends AbstractApiClient {

    @Autowired
    private StencilsAdminApi stencilsAdminApi;

    public List<StencilResponse> getAllStencils() {
        return executeApiFunction(() -> stencilsAdminApi.getStencils().execute());
    }

    public StencilResponse updateStencil(
            String stencilUuid,
            StencilUpdateRequest stencilUpdateRequest) {
        return executeApiFunction(() -> stencilsAdminApi.updateStencil(
                stencilUuid, stencilUpdateRequest).execute());
    }

    public StencilResponse createStencil(StencilCreateRequest stencilCreateRequest) {
        return executeApiFunction(() -> stencilsAdminApi.createStencil(stencilCreateRequest).execute());
    }
}
