package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.PluginsClient;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RouteScope
@SpringComponent
public class PluginsAccessor {

    @Autowired
    private PluginsClient pluginsClient;

    public List<PluginResponse> getAllPlugins() {
        return pluginsClient.getAllPlugins();
    }

    public Map<String, PluginResponse> getPluginsUuidMap() {
        return getAllPlugins().stream()
                .collect(Collectors.toMap(PluginResponse::getPluginUuid, Function.identity()));
    }

    public List<String> getAvailableFactories() {
        return pluginsClient.getFactories();
    }

    public void updatePlugin(String pluginUuid, PluginUpdateRequest updateRequest) {
        pluginsClient.updatePlugin(pluginUuid, updateRequest);
    }

    public void createPlugin(PluginCreateRequest createRequest) {
        pluginsClient.createPlugin(createRequest);
    }

    public void deletePlugin(String pluginUuid) {
        pluginsClient.deletePlugin(pluginUuid);
    }
}
