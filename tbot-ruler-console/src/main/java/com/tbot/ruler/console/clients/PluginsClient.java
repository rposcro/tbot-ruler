package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PluginsClient extends AbstractApiClient {

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

    public void createPlugin(PluginCreateRequest createRequest) {
        executeApiFunction(() -> pluginsAdminApi.createPlugin(createRequest).execute());
    }
}
