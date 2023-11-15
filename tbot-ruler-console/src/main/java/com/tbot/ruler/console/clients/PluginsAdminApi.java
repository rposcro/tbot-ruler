package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface PluginsAdminApi {

    @GET("/admin/plugins")
    Call<List<PluginResponse>> getPlugins();

    @GET("/admin/plugins/factories")
    Call<List<String>> getFactories();

    @POST("/admin/plugins")
    Call<PluginResponse> createPlugin(@Body PluginCreateRequest createPluginRequest);

    @PATCH("/admin/plugins/{pluginUuid}")
    Call<PluginResponse> updatePlugin(
            @Path("pluginUuid") String pluginUuid,
            @Body PluginUpdateRequest updatePluginRequest);

    @DELETE("/admin/plugins/{pluginUuid}")
    Call<PluginResponse> deletePlugin(@Path("pluginUuid") String pluginUuid);
}
