package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.WebhookCreateRequest;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.tbot.ruler.controller.admin.payload.WebhookUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface WebhooksAdminApi {

    @GET("/admin/webhooks")
    Call<List<WebhookResponse>> getWebhooks();

    @GET("/admin/webhooks/owners")
    Call<List<String>> getOwners();

    @POST("/admin/webhooks")
    Call<WebhookResponse> createWebhook(@Body WebhookCreateRequest webhookCreateRequest);

    @PATCH("/admin/webhooks/{webhookUuid}")
    Call<WebhookResponse> updateWebhook(
            @Path("webhookUuid") String webhookUuid,
            @Body WebhookUpdateRequest webhookUpdateRequest);

    @DELETE("/admin/webhooks/{webhookUuid}")
    Call<WebhookResponse> deleteWebhook(@Path("webhookUuid") String webhookUuid);
}
