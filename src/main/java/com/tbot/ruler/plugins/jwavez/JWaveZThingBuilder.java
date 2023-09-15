package com.tbot.ruler.plugins.jwavez;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.BasicThing;
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

        _JWaveZThingContext thingContext = _JWaveZThingContext.builder()
                .builderContext(builderContext)
                .messagePublisher(builderContext.getMessagePublisher())
                .jwzApplicationSupport(JwzApplicationSupport.defaultSupport())
                .jwzCommandSender(commandSender)
                .build();

        _JWaveZThingItemsBuilder itemsBuilder = new _JWaveZThingItemsBuilder(thingContext);
        List<Actuator> actuators = itemsBuilder.buildActuators(builderContext);
        registerListeners(serialHandler, itemsBuilder);

        ThingDTO thingDTO = builderContext.getThingDTO();

        return BasicThing.builder()
            .uuid(thingDTO.getUuid())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .actuators(actuators)
            .startUpTask(() -> serialController.connect())
            .triggerableTask(commandSender)
            .build();
    }

    private void registerListeners(JWaveZSerialHandler serialHandler, _JWaveZThingItemsBuilder itemsBuilder) {
        itemsBuilder.getActuatorBuilderMap().values().stream()
                .filter(builder -> builder.getSupportedCommandType() != null && builder.getSupportedCommandHandler() != null)
                .forEach(builder -> serialHandler.addCommandListener(builder.getSupportedCommandType(), builder.getSupportedCommandHandler()));
    }

    private JWaveZPluginConfiguration parseThingConfiguration(ThingBuilderContext builderContext) throws PluginException {
        try {
            return new ObjectMapper().readerFor(JWaveZPluginConfiguration.class).readValue(builderContext.getThingDTO().getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse JWaveZ thing's configuration!", e);
        }
    }
}
