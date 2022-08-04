package com.tbot.ruler.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.tbot.ruler.broker.MessageQueue;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.exceptions.PluginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tbot.ruler.things.service.ServiceProvider;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ThingsConfiguration {
    
    @Autowired
    private ServiceProvider serviceProvider;

    @Autowired
    private MessageQueue messageQueue;

    @Bean
    public List<Thing> things(List<ThingDTO> thingDTOs, List<ThingPluginDTO> pluginDTOs) {
        Map<String, ThingPluginDTO> pluginPerAlias = pluginPerAlias(pluginDTOs);
        List<Thing> things = new ArrayList<>(thingDTOs.size());
        
        thingDTOs.forEach(thingDTO -> {
            try {
                log.info("Building thing: {} {} {}", thingDTO.getId(), thingDTO.getName(), thingDTO.getPluginAlias());
                Thing thing = buildThing(thingDTO, pluginPerAlias);
                things.add(thing);
            } catch(ReflectiveOperationException e) {
                log.error("Failed to instantiate builder!", e);
            } catch(IllegalArgumentException e) {
                log.error("Missing plugin alias " + thingDTO.getPluginAlias() + "! Not registered plugin?!", e);
            } catch(PluginException e) {
                log.error("Exception occurred while instantiating thing", e);
            }
        });
        
        return things;
    }

    private Thing buildThing(ThingDTO thingDTO, Map<String, ThingPluginDTO> pluginPerAlias) 
    throws ReflectiveOperationException, PluginException {
        ThingPluginDTO pluginDTO = Optional.ofNullable(pluginPerAlias.get(thingDTO.getPluginAlias()))
                .orElseThrow(IllegalArgumentException::new);
        ThingBuilderContext builderContext = ThingBuilderContext.builder()
                .services(serviceProvider)
                .thingDTO(thingDTO)
                .pluginDTO(pluginDTO)
                .messagePublisher(messageQueue)
                .build();
        Thing thing = instantiateBuilder(pluginDTO)
                .buildThing(builderContext);
        return thing;
    }
    
    @SuppressWarnings("unchecked")
    private ThingPluginBuilder instantiateBuilder(ThingPluginDTO pluginDTO) throws ReflectiveOperationException {
        String builderClassName = pluginDTO.getBuilder();
        Class<?> builderClass = ThingsConfiguration.class.getClassLoader().loadClass(builderClassName);
        Object builder = builderClass.newInstance();
        return (ThingPluginBuilder) builder;
    }
    
    private Map<String, ThingPluginDTO> pluginPerAlias(List<ThingPluginDTO> pluginDTOs) {
        return pluginDTOs.stream().
                collect(Collectors.toMap(ThingPluginDTO::getAlias, Function.identity()));
    }
}
