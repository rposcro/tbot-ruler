package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.PluginUpdateRequest;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.service.StructureService;
import com.tbot.ruler.service.lifecycle.PluginsLifecycleService;
import com.tbot.ruler.service.manipulators.PluginsManipulator;
import com.tbot.ruler.subjects.plugin.Plugin;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.internalServerError;

@Slf4j
@RestController
@RequestMapping(path = "/admin/plugins")
public class PluginsAdminController extends AbstractController {

    @Autowired
    private StructureService structureService;

    @Autowired
    private RepositoryAccessor subjectsAccessor;

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private PluginsManipulator pluginsManipulator;

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @GetMapping
    public ResponseEntity<List<PluginResponse>> getAllPlugins() {
        return ok(pluginsRepository.findAll().stream()
                .map(this::toResponse)
                .toList());
    }

    @GetMapping("/factories")
    public ResponseEntity<List<String>> getFactories() {
        return ok(structureService.getPluginsFactories().stream()
                .map(Class::getName)
                .toList());
    }

    @PostMapping
    public ResponseEntity<PluginResponse> createPlugin(@RequestBody PluginCreateRequest pluginCreateRequest) {
        PluginEntity pluginEntity = pluginsManipulator.createPlugin(PluginEntity.builder()
                .pluginUuid("plgn-" + UUID.randomUUID())
                .name(pluginCreateRequest.getName())
                .factoryClass(pluginCreateRequest.getFactoryClass())
                .configuration(pluginCreateRequest.getConfiguration())
                .build());

        Plugin plugin = pluginsLifecycleService.getPluginById(pluginEntity.getPluginId());
        if (plugin == null) {
            return internalServerError().build();
        }

        return ok(toResponse(pluginEntity));
    }

    @PatchMapping("/{pluginUuid}")
    public ResponseEntity<PluginResponse> updatePlugin(
            @PathVariable String pluginUuid,
            @RequestBody PluginUpdateRequest pluginUpdateRequest) {
        PluginEntity pluginEntity = subjectsAccessor.findPlugin(pluginUuid);
        pluginEntity.setName(pluginUpdateRequest.getName());
        pluginEntity.setConfiguration(pluginUpdateRequest.getConfiguration());

        pluginEntity = pluginsRepository.save(pluginEntity);
        return ok(toResponse(pluginEntity));
    }

    @DeleteMapping("/{pluginUuid}")
    public ResponseEntity<PluginResponse> deletePlugin(@PathVariable String pluginUuid) {
        PluginEntity pluginEntity = subjectsAccessor.findPlugin(pluginUuid);
        pluginsRepository.delete(pluginEntity);
        return ok(toResponse(pluginEntity));
    }

    private PluginResponse toResponse(PluginEntity entity) {
        List<String> supportedActuatorReferences = new ArrayList<>(pluginsLifecycleService.getPluginById(entity.getPluginId())
            .getSupportedActuatorReferences());
        supportedActuatorReferences.sort(Comparator.naturalOrder());

        return PluginResponse.builder()
                .pluginUuid(entity.getPluginUuid())
                .name(entity.getName())
                .factoryClass(entity.getFactoryClass())
                .configuration(entity.getConfiguration())
                .supportedActuatorReferences(supportedActuatorReferences)
                .build();
    }
}
