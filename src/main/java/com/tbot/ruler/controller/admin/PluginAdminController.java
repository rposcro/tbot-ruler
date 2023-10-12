package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.CreatePluginRequest;
import com.tbot.ruler.controller.admin.payload.UpdatePluginRequest;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@RestController
@RequestMapping(path = "/admin/plugins")
public class PluginAdminController extends AbstractController {

    @Autowired
    private PluginsRepository pluginsRepository;

    @GetMapping
    public ResponseEntity<List<PluginEntity>> getAllPlugins() {
        return ok(pluginsRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<PluginEntity> createPlugin(@RequestBody CreatePluginRequest createPluginRequest) {
        PluginEntity pluginEntity = pluginsRepository.save(PluginEntity.builder()
                .pluginUuid("plgn-" + UUID.randomUUID())
                .name(createPluginRequest.getName())
                .factoryClass(createPluginRequest.getBuilderClass())
                .configuration(createPluginRequest.getConfiguration())
                .build());
        return ok(pluginEntity);
    }

    @PatchMapping("/{pluginUuid}")
    public ResponseEntity<PluginEntity> updatePlugin(
            @PathVariable String pluginUuid,
            @RequestBody UpdatePluginRequest updatePluginRequest) {
        PluginEntity pluginEntity = findPlugin(pluginUuid);
        pluginEntity.setName(updatePluginRequest.getName());
        pluginEntity.setConfiguration(updatePluginRequest.getConfiguration());

        pluginEntity = pluginsRepository.save(pluginEntity);
        return ok(pluginEntity);
    }

    @DeleteMapping("/{pluginUuid}")
    public ResponseEntity<PluginEntity> deletePlugin(@PathVariable String pluginUuid) {
        PluginEntity pluginEntity = findPlugin(pluginUuid);
        pluginsRepository.delete(pluginEntity);
        return ok(pluginEntity);
    }

    private PluginEntity findPlugin(String pluginUuid) {
        return pluginsRepository.findByUuid(pluginUuid)
                .orElseThrow(() -> new ServiceRequestException(format("Plugin %s not found!", pluginUuid)));
    }
}
