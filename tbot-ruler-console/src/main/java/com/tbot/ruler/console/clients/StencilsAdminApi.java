package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.StencilCreateRequest;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.tbot.ruler.controller.admin.payload.StencilUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface StencilsAdminApi {

    @GET("admin/stencils")
    Call<List<StencilResponse>> getStencils();

    @PATCH("admin/stencils/{stencilUuid}")
    Call<StencilResponse> updateStencil(
            @Path("stencilUuid") String stencilUuid,
            @Body StencilUpdateRequest stencilUpdateRequest);

    @POST("admin/stencils")
    Call<StencilResponse> createStencil(@Body StencilCreateRequest stencilCreateRequest);
}
