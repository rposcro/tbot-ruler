package com.tbot.ruler.persistance.json;

import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.persistance.json.dto.ActuatorDTO;
import com.tbot.ruler.persistance.json.dto.BindingDTO;
import com.tbot.ruler.persistance.json.dto.StencilDTO;
import com.tbot.ruler.persistance.json.dto.ThingDTO;
import com.tbot.ruler.persistance.json.dto.PluginDTO;
import com.tbot.ruler.persistance.json.dto.WebhookDTO;
import com.tbot.ruler.util.FileUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JsonFileRepositoryReader {

    private final FileUtil fileUtil;
    private List<WrapperDTO> dtoWrappers;

    @Builder
    public JsonFileRepositoryReader(String configPath, FileUtil fileUtil) {
        this.fileUtil = fileUtil;
        initialize(configPath);
    }

    public void initialize(String configPath) {
        this.dtoWrappers = new LinkedList<>();
        dtoWrappers.addAll(fileUtil.deserializeJsonFilesInSubPackages(configPath, WrapperDTO.class));
        validateUuids();
    }

    public List<PluginDTO> getPluginDTOs() {
        Set<String> aliases = new HashSet<>();
        List<PluginDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.plugins != null)
                .flatMap(wrapper -> wrapper.plugins.stream())
                .filter(dto -> {
                    if (aliases.contains(dto.getAlias())) {
                        log.info("Skipping plugin {}. Alias already exists!", dto);
                        return false;
                    } else {
                        aliases.add(dto.getAlias());
                        return true;
                    }
                })
                .collect(Collectors.toList());
        log.info("Found and read {} plugin DTOs", dtos.size());
        return dtos;
    }

    public PluginDTO getPluginDTO(String pluginUuid) {
        return dtoWrappers.stream()
                .filter(wrapper -> wrapper.plugins != null)
                .flatMap(wrapper -> wrapper.plugins.stream())
                .filter(plugin -> pluginUuid.equals(plugin.getUuid()))
                .findFirst()
                .orElse(null);
    }

    public List<ThingDTO> getThingDTOs() {
        List<ThingDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.things != null)
                .flatMap(wrapper -> wrapper.things.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} thing DTOs", dtos.size());
        return dtos;
    }

    public ThingDTO getThingDTO(String thingUuid) {
        return dtoWrappers.stream()
                .filter(wrapper -> wrapper.things != null)
                .flatMap(wrapper -> wrapper.things.stream())
                .filter(thing -> thingUuid.equals(thing.getUuid()))
                .findFirst()
                .orElse(null);
    }

    public List<ActuatorDTO> getActuatorDTOs() {
        List<ActuatorDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.actuators != null)
                .flatMap(wrapper -> wrapper.actuators.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} actuator DTOs", dtos.size());
        return dtos;
    }

    public List<WebhookDTO> getWebhookDTOs() {
        List<WebhookDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.webhooks != null)
                .flatMap(wrapper -> wrapper.webhooks.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} webhook DTOs", dtos.size());
        return dtos;
    }

    public List<BindingDTO> getBindingDTOs() {
        List<BindingDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.bindings != null)
                .flatMap(wrapper -> wrapper.bindings.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} binding DTOs", dtos.size());
        return dtos;
    }

    public List<StencilDTO> getStencilDTOs() {
        List<StencilDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.stencils != null)
                .flatMap(wrapper -> wrapper.stencils.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} stencils DTOs", dtos.size());
        return dtos;
    }

    private void validateUuids() {
        Set<String> uuids = new HashSet<>();
        getPluginDTOs().stream().forEach(dto -> considerUuid(uuids, dto.getUuid()));
        getThingDTOs().stream().forEach(dto -> considerUuid(uuids, dto.getUuid()));
    }

    private void considerUuid(Set<String> uuids, String uuid) {
        if (uuids.contains(uuid)) {
            throw new ConfigurationException("Repeated uuid: " + uuid);
        }
        uuids.add(uuid);
    }

    @Data
    private static class WrapperDTO {
        private List<PluginDTO> plugins;
        private List<ThingDTO> things;
        private List<ActuatorDTO> actuators;
        private List<WebhookDTO> webhooks;
        private List<BindingDTO> bindings;
        private List<StencilDTO> stencils;
    }
}
