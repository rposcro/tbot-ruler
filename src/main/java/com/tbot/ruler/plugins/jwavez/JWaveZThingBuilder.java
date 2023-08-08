package com.tbot.ruler.plugins.jwavez;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.exceptions.PluginException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JWaveZThingBuilder implements ThingPluginBuilder {

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        JWaveZSerialHandler serialHandler = new JWaveZSerialHandler();
        JWaveZSerialController serialController = JWaveZSerialController.builder()
                .configuration(parseThingConfiguration(builderContext))
                .callbackHandler(serialHandler)
                .build();
        JWaveZCommandSender commandSender = JWaveZCommandSender.builder()
                .jwzController(serialController)
                .build();

        JWaveZThingContext thingContext = JWaveZThingContext.builder()
                .builderContext(builderContext)
                .messagePublisher(builderContext.getMessagePublisher())
                .jwzApplicationSupport(JwzApplicationSupport.defaultSupport())
                .jwzCommandSender(commandSender)
                .build();

        JWaveZThingItemsBuilder itemsBuilder = new JWaveZThingItemsBuilder(thingContext);
        List<Emitter> emitters = itemsBuilder.buildEmitters(builderContext);
        List<Collector> collectors = itemsBuilder.buildCollectors(builderContext);
        List<Actuator> actuators = itemsBuilder.buildActuators(builderContext);
        registerListeners(serialHandler, itemsBuilder);

        ThingDTO thingDTO = builderContext.getThingDTO();

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .emitters(emitters)
            .collectors(collectors)
            .actuators(actuators)
            .startUpTask(() -> serialController.connect())
            .triggerableTask(commandSender)
            .build();
    }

    private void registerListeners(JWaveZSerialHandler serialHandler, JWaveZThingItemsBuilder itemsBuilder) {
        itemsBuilder.getActuatorBuilderMap().values().stream()
                .forEach(builder -> serialHandler.addCommandListener(builder.getSupportedCommandType(), builder.getSupportedCommandHandler()));
        itemsBuilder.getEmitterBuilderMap().values().stream()
                .forEach(builder -> serialHandler.addCommandListener(builder.getSupportedCommandType(), builder.getSupportedCommandHandler()));
    }

    private JWaveZThingConfiguration parseThingConfiguration(ThingBuilderContext builderContext) throws PluginException {
        try {
            return new ObjectMapper().readerFor(JWaveZThingConfiguration.class).readValue(builderContext.getThingDTO().getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse JWaveZ thing's configuration!", e);
        }
    }
}
