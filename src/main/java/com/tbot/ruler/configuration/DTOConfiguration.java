package com.tbot.ruler.configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.things.ActuatorId;
import com.tbot.ruler.things.CollectorId;
import com.tbot.ruler.things.EmitterId;
import com.tbot.ruler.things.ThingId;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.util.FileUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class DTOConfiguration {

    @Value("${ruler.thingsConfig.path}")
    private String configPath;
    @Autowired
    private FileUtil fileUtil;

    private List<WrapperDTO> dtoWrappers;

    @PostConstruct
    public void initialize() {
        dtoWrappers = fileUtil.deserializeJsonFilesInSubPackages(configPath, WrapperDTO.class);
    }

    @Bean
    public List<ApplianceDTO> applianceDTOs() {
        log.debug("Reading appliances DTO from {}", configPath);
        List<ApplianceDTO> dtos = dtoWrappers.stream()
            .map(wrapper -> Optional.ofNullable(wrapper.getAppliances()).orElse(Collections.emptyList()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        log.info("Found and read {} appliance DTOs", dtos.size());
        return dtos;
    }

    @Bean
    public Map<ApplianceId, ApplianceDTO> applianceDTOMap() {
        return applianceDTOs().stream()
            .collect(Collectors.toMap(ApplianceDTO::getId, Function.identity()));
    }

    @Bean
    public List<EmitterDTO> emitterDTOs() {
        return thingDTOs().stream()
            .flatMap(thingDTO -> thingDTO.getEmitters().stream())
            .collect(Collectors.toList());
    }

    @Bean
    public Map<EmitterId, EmitterDTO> emitterDTOMap() {
        return emitterDTOs().stream()
            .collect(Collectors.toMap(EmitterDTO::getId, Function.identity()));
    }

    @Bean
    public List<CollectorDTO> collectorDTOs() {
        return thingDTOs().stream()
            .flatMap(thingDTO -> thingDTO.getCollectors().stream())
            .collect(Collectors.toList());
    }

    @Bean
    public Map<CollectorId, CollectorDTO> collectorDTOMap() {
        return collectorDTOs().stream()
            .collect(Collectors.toMap(CollectorDTO::getId, Function.identity()));
    }

    @Bean
    public List<ActuatorDTO> actuatorDTOs() {
        return thingDTOs().stream()
            .flatMap(thingDTO -> thingDTO.getActuators().stream())
            .collect(Collectors.toList());
    }

    @Bean
    public Map<ActuatorId, ActuatorDTO> actuatorDTOMap() {
        return actuatorDTOs().stream()
            .collect(Collectors.toMap(ActuatorDTO::getId, Function.identity()));
    }

    @Bean
    public List<ThingDTO> thingDTOs() {
        log.debug("Reading things DTO from {}", configPath);
        List<ThingDTO> dtos = dtoWrappers.stream()
            .map(wrapper -> Optional.ofNullable(wrapper.getThings()).orElse(Collections.emptyList()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        log.info("Found and read {} thing DTOs", dtos.size());
        return dtos;
    }

    @Bean
    public Map<ThingId, ThingDTO> thingDTOMap() {
        return thingDTOs().stream()
            .collect(Collectors.toMap(ThingDTO::getId, Function.identity()));
    }

    @Bean
    public List<BindingDTO> bindingDTOs() {
        log.debug("Reading bindings DTO from {}", configPath);
        List<BindingDTO> dtos = dtoWrappers.stream()
            .map(wrapper -> Optional.ofNullable(wrapper.getBindings()).orElse(Collections.emptyList()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        log.info("Found and read {} binding DTOs", dtos.size());
        return dtos;
    }

    @Bean
    public Map<ApplianceId, BindingDTO> applianceBindingDTOMap() {
        return bindingDTOs().stream()
            .collect(Collectors.toMap(bindingDto -> bindingDto.getApplianceId(), Function.identity()));
    }

    @Bean
    public List<ThingPluginDTO> pluginDTOs() {
        log.debug("Reading plugins DTO from {}", configPath);
        Set<String> aliases = new HashSet<>();
        List<ThingPluginDTO> dtos = dtoWrappers.stream()
            .map(wrapper -> Optional.ofNullable(wrapper.getPlugins()).orElse(Collections.emptyList()))
            .flatMap(List::stream)
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

    @Data
    private static class WrapperDTO {
        private List<ThingPluginDTO> plugins;
        private List<ThingDTO> things;
        private List<ApplianceDTO> appliances;
        private List<BindingDTO> bindings;
    }
}
