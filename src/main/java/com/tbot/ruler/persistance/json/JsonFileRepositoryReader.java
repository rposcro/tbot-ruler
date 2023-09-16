package com.tbot.ruler.persistance.json;

import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;
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
        dtoWrappers.addAll(fileUtil.deserializeJsonFilesInSubPackages(configPath + "/plugins", WrapperDTO.class));
        dtoWrappers.addAll(fileUtil.deserializeJsonFilesInSubPackages(configPath + "/things", WrapperDTO.class));
        dtoWrappers.addAll(fileUtil.deserializeJsonFilesInSubPackages(configPath + "/appliances", WrapperDTO.class));
        dtoWrappers.addAll(fileUtil.deserializeJsonFilesInSubPackages(configPath + "/bindings", WrapperDTO.class));
        validateUuids();
    }

    public List<ApplianceDTO> getApplianceDTOs() {
        List<ApplianceDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.appliances != null)
                .flatMap(wrapper -> wrapper.appliances.stream())
                .collect(Collectors.toList());
        return dtos;
    }

    public List<ThingPluginDTO> getPluginDTOs() {
        Set<String> aliases = new HashSet<>();
        List<ThingPluginDTO> dtos = dtoWrappers.stream()
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
        return dtos;
    }

    public ThingPluginDTO getPluginDTO(String uuid) {
        return dtoWrappers.stream()
                .filter(wrapper -> wrapper.plugins != null)
                .flatMap(wrapper -> wrapper.plugins.stream())
                .filter(plugin -> uuid.equals(plugin.getUuid()))
                .findFirst()
                .orElse(null);
    }

    public List<ThingDTO> getThingDTOs() {
        Set<String> aliases = new HashSet<>();
        List<ThingDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.things != null)
                .flatMap(wrapper -> wrapper.things.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} thing DTOs", dtos.size());
        return dtos;
    }

    public ThingDTO getThingDTO(String uuid) {
        return dtoWrappers.stream()
                .filter(wrapper -> wrapper.things != null)
                .flatMap(wrapper -> wrapper.things.stream())
                .filter(thing -> uuid.equals(thing.getUuid()))
                .findFirst()
                .orElse(null);
    }

    public List<BindingDTO> getBindingDTOs() {
        Set<String> aliases = new HashSet<>();
        List<BindingDTO> dtos = dtoWrappers.stream()
                .filter(wrapper -> wrapper.bindings != null)
                .flatMap(wrapper -> wrapper.bindings.stream())
                .collect(Collectors.toList());
        log.info("Found and read {} thing DTOs", dtos.size());
        return dtos;
    }

    private void validateUuids() {
        Set<String> uuids = new HashSet<>();
        getPluginDTOs().stream().forEach(dto -> considerUuid(uuids, dto.getUuid()));
        getThingDTOs().stream().forEach(dto -> considerUuid(uuids, dto.getUuid()));
        getApplianceDTOs().stream().forEach(dto -> considerUuid(uuids, dto.getUuid()));
    }

    private void considerUuid(Set<String> uuids, String uuid) {
        if (uuids.contains(uuid)) {
            throw new ConfigurationException("Repeated uuid: " + uuid);
        }
        uuids.add(uuid);
    }

    @Data
    private static class WrapperDTO {
        private List<ThingPluginDTO> plugins;
        private List<ThingDTO> things;
        private List<ApplianceDTO> appliances;
        private List<BindingDTO> bindings;
    }
}
