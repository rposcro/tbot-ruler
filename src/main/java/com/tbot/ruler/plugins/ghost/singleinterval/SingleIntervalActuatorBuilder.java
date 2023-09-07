package com.tbot.ruler.plugins.ghost.singleinterval;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.messages.MessagePublisher;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.plugins.ghost.GhostThingConfiguration;
import com.tbot.ruler.things.Actuator;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import com.tbot.ruler.things.thread.RegularEmissionTrigger;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Optional;

public class SingleIntervalActuatorBuilder implements GhostActuatorBuilder {

    private static final String REFERENCE = "single-interval";

    @Override
    public String getReference() {
        return REFERENCE;
    }

    @Override
    public Actuator buildActuator(ActuatorDTO actuatorDTO, MessagePublisher messagePublisher, GhostThingConfiguration ghostThingConfiguration) throws PluginException {
        SingleIntervalConfiguration configuration = extractConfiguration(actuatorDTO);
        SingleIntervalStateAgent stateAgent = new SingleIntervalStateAgent();
        Optional<Runnable> emissionTask = Optional.of(SingleIntervalEmissionTask.builder()
                .configuration(configuration)
                .emitterId(actuatorDTO.getId())
                .messagePublisher(messagePublisher)
                .zoneId(ZoneId.of(ghostThingConfiguration.getTimeZone()))
                .stateAgent(stateAgent)
                .build());

        return SingleIntervalActuator.builder()
                .id(actuatorDTO.getId())
                .name(actuatorDTO.getName())
                .description(actuatorDTO.getDescription())
                .singleIntervalStateAgent(stateAgent)
                .startUpTask(emissionTask)
                .triggerableTask(emissionTask)
                .taskTrigger(Optional.of(new RegularEmissionTrigger(configuration.getEmissionIntervalMinutes() * 60_000)))
                .build();
    }

    private SingleIntervalConfiguration extractConfiguration(ActuatorDTO actuatorDTO) throws PluginException {
        try {
            return new ObjectMapper().readerFor(SingleIntervalConfiguration.class).readValue(actuatorDTO.getConfigurationNode());
        } catch(IOException e) {
            throw new PluginException("Could not parse Ghost Single Interval configuration!", e);
        }
    }
}
