package com.tbot.ruler.console.clients;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PluginsClient {

    @Autowired
    private PluginsAdminApi pluginsAdminApi;

    public List<PluginResponse> getAllPlugins() {
        return executeApiFunction(() -> pluginsAdminApi.getPlugins().execute());
    }

    public List<String> getFactories() {
        return executeApiFunction(() -> pluginsAdminApi.getFactories().execute());
    }

    public void updatePlugin(String pluginUuid, PluginUpdateRequest updateRequest) {
        executeApiFunction(() -> pluginsAdminApi.updatePlugin(pluginUuid, updateRequest).execute());
    }

    private <T> T executeApiFunction(ApiFunction<Response<T>> function) {
        try {
            Response<T> response = function.runProcedure();
            if (!response.isSuccessful()) {
                throw new ClientCommunicationException(response.code(), "Error %s from TBot Ruler Service!", response.code());
            }
            return response.body();
        } catch(IOException e) {
            log.error("Exception from TBot Ruler Service", e);
            throw new ClientCommunicationException("Exception from TBot Ruler Service: " + e.getMessage(), e);
        }
    }
}
