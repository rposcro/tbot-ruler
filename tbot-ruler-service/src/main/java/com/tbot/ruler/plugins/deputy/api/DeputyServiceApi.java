package com.tbot.ruler.plugins.deputy.api;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeputyServiceApi {

    @PATCH("binary-outputs/{pinNumber}")
    Call<Response<?>> setBinaryState(@Path("pinNumber") int pinNumber, @Query("state") String state);

    @GET("binary-outputs/{pinNumber}")
    Call<BinOutStateResponse> getBinaryState(@Path("pinNumber") int pinNumber);

    @GET("")
    Call<Response<?>> ping();
}
