package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.ThingCreateRequest;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.tbot.ruler.controller.admin.payload.ThingUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ThingsAdminApi {

    @GET("/admin/things")
    Call<List<ThingResponse>> getThings();

    @POST("/admin/things")
    Call<ThingResponse> createThing(@Body ThingCreateRequest thingCreateRequest);

    @PATCH("/admin/things/{thingUuid}")
    Call<ThingResponse> updateThing(
            @Path("thingUuid") String thingUuid,
            @Body ThingUpdateRequest thingUpdateRequest);

    @DELETE("/admin/things/{thingUuid}")
    Call<ThingResponse> deleteThing(@Path("thingUuid") String thingUuid);
}
