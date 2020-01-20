package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.plugins.jwavez.basicset.BasicSetActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.sceneactivation.SceneActivationEmitterBuilder;
import com.tbot.ruler.plugins.jwavez.switchcolor.SwitchColorActuatorBuilder;
import com.tbot.ruler.plugins.jwavez.switchmultilevel.SwitchMultilevelCollectorBuilder;
import com.tbot.ruler.things.*;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.ThingBuilderContext;
import com.tbot.ruler.things.builder.ThingPluginBuilder;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class JWaveZBuilder implements ThingPluginBuilder {

    private static final String EMITTER_TYPE_SCENE_ACTIVATION = "scene-activation";
    private static final String ACTUATOR_TYPE_BASIC_SET = "basic-set";
    private static final String ACTUATOR_TYPE_SWITCH_COLOR = "switch-color";
    private static final String COLLECTOR_TYPE_SWITCH_MULTILEVEL = "switch-multilevel";

    private BasicSetActuatorBuilder basicSetEmitterBuilder;
    private SceneActivationEmitterBuilder sceneActivationEmitterBuilder;
    private SwitchMultilevelCollectorBuilder switchMultilevelCollectorBuilder;
    private SwitchColorActuatorBuilder switchColorActuatorBuilder;

    public JWaveZBuilder() {
        this.basicSetEmitterBuilder = new BasicSetActuatorBuilder();
        this.sceneActivationEmitterBuilder = new SceneActivationEmitterBuilder();
        this.switchMultilevelCollectorBuilder = new SwitchMultilevelCollectorBuilder();
        this.switchColorActuatorBuilder = new SwitchColorActuatorBuilder();
    }

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        JWaveZAgent agent = agent(builderContext);
        ThingDTO thingDTO = builderContext.getThingDTO();

        return BasicThing.builder()
            .id(thingDTO.getId())
            .name(thingDTO.getName())
            .description(thingDTO.getDescription())
            .emitters(buildEmitters(builderContext, agent))
            .actuators(buildActuators(builderContext, agent))
            .collectors(buildCollectors(builderContext, agent))
            .startUpTask(() -> agent.connect())
            .build();
    }

    private JWaveZAgent agent(ThingBuilderContext builderContext) {
        return new JWaveZAgent(builderContext);
    }

    private List<Actuator> buildActuators(ThingBuilderContext builderContext, JWaveZAgent agent) throws PluginException {
        List<Actuator> actuators = new LinkedList<>();
        for (ActuatorDTO actuatorDTO : builderContext.getThingDTO().getActuators()) {
            actuators.add(buildActuator(agent, builderContext, actuatorDTO));
        }
        return actuators;
    }

    private Actuator buildActuator(JWaveZAgent agent, ThingBuilderContext context, ActuatorDTO actuatorDTO) throws PluginException {
        switch(actuatorDTO.getRef()) {
            case ACTUATOR_TYPE_BASIC_SET:
                return basicSetEmitterBuilder.buildActuator(agent.getBasicSetHandler(), context, actuatorDTO);
            case ACTUATOR_TYPE_SWITCH_COLOR:
                return switchColorActuatorBuilder.buildActuator(agent, context, actuatorDTO);
            default:
                throw new PluginException("Unknown actuator reference " + actuatorDTO.getRef() + ", skipping this DTO");
        }
    }

    private List<Emitter> buildEmitters(ThingBuilderContext builderContext, JWaveZAgent agent) throws PluginException {
        List<Emitter> emitters = new LinkedList<>();
        for (EmitterDTO emitterDTO : builderContext.getThingDTO().getEmitters()) {
            emitters.add(buildEmitter(agent, builderContext, emitterDTO));
        }
        return emitters;
    }

    private Emitter buildEmitter(JWaveZAgent agent, ThingBuilderContext context, EmitterDTO emitterDTO) throws PluginException {
        switch(emitterDTO.getRef()) {
            case EMITTER_TYPE_SCENE_ACTIVATION:
                return sceneActivationEmitterBuilder.buildEmitter(agent.getSceneActivationHandler(), context, emitterDTO);
            default:
                throw new PluginException("Unknown emitter reference " + emitterDTO.getRef() + ", skipping this DTO");
        }
    }

    private List<Collector> buildCollectors(ThingBuilderContext builderContext, JWaveZAgent agent) throws PluginException {
        List<Collector> collectors = new LinkedList<>();
        for (CollectorDTO collectorDTO : builderContext.getThingDTO().getCollectors()) {
            collectors.add(buildCollector(agent, builderContext, collectorDTO));
        }
        return collectors;
    }

    private Collector buildCollector(JWaveZAgent agent, ThingBuilderContext context, CollectorDTO collectorDTO) throws PluginException {
        switch(collectorDTO.getRef()) {
            case COLLECTOR_TYPE_SWITCH_MULTILEVEL:
                return switchMultilevelCollectorBuilder.buildCollector(collectorDTO, agent);
            default:
                throw new PluginException("Unknown collector reference " + collectorDTO.getRef() + ", skipping this DTO");
        }
    }
}
