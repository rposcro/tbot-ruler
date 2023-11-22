package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.PluginsClient;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RouteScope
@SpringComponent
public class RoutePluginsAccessor {

    @Autowired
    private PluginsClient pluginsClient;

    public List<PluginResponse> getAllPlugins() {
        return pluginsClient.getAllPlugins();
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
}
