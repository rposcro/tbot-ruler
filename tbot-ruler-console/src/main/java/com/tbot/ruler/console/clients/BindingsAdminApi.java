package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.BindingCreateRequest;
import com.tbot.ruler.controller.admin.payload.BindingDeleteRequest;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface BindingsAdminApi {

    @GET("/admin/bindings")
    Call<List<BindingResponse>> getBindings();

    @GET("/admin/bindings/senders/{senderUuid}")
    Call<List<BindingResponse>> getSenderBindings(@Path("senderUuid") String senderUuid);

    @GET("/admin/bindings/receivers/{receiverUuid}")
    Call<List<BindingResponse>> getReceiverBindings(@Path("receiverUuid") String receiverUuid);

    @POST("/admin/bindings")
    Call<BindingResponse> createThing(@Body BindingCreateRequest request);

    @HTTP(method = "DELETE", path = "/admin/bindings", hasBody = true)
    Call<BindingResponse> deleteThing(@Body BindingDeleteRequest request);
}
