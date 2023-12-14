package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.ActuatorCreateRequest;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.ActuatorUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ActuatorsAdminApi {

    @GET("admin/actuators")
    Call<List<ActuatorResponse>> getActuators();

    @POST("admin/actuators")
    Call<ActuatorResponse> createActuator(@Body ActuatorCreateRequest actuatorCreateRequest);

    @PATCH("admin/actuators/{actuatorUuid}")
    Call<ActuatorResponse> updateActuator(
            @Path("actuatorUuid") String actuatorUuid,
            @Body ActuatorUpdateRequest actuatorUpdateRequest);

    @DELETE("admin/actuators/{actuatorUuid}")
    Call<ActuatorResponse> deleteActuator(@Path("actuatorUuid") String actuatorUuid);
}
