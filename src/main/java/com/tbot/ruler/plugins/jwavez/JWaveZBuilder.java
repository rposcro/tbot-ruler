package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.things.BasicThing;
import com.tbot.ruler.things.EmissionThread;
import com.tbot.ruler.things.Emitter;
import com.tbot.ruler.things.Thing;
import com.tbot.ruler.things.ThingBuilderContext;
import com.tbot.ruler.things.ThingMetadata;
import com.tbot.ruler.things.ThingPluginBuilder;
import com.tbot.ruler.things.dto.EmitterDTO;
import com.tbot.ruler.things.exceptions.PluginException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class JWaveZBuilder implements ThingPluginBuilder {

    private static final String EMITTER_TYPE_SCENE_ACTIVATION = "scene-activation";
    private static final String EMITTER_TYPE_BASIC_SET = "basic-set";

    private BasicSetEmitterBuilder basicSetEmitterBuilder;
    private SceneActivationEmitterBuilder sceneActivationEmitterBuilder;

    public JWaveZBuilder() {
        this.basicSetEmitterBuilder = new BasicSetEmitterBuilder();
        this.sceneActivationEmitterBuilder = new SceneActivationEmitterBuilder();
    }

    @Override
    public Thing buildThing(ThingBuilderContext builderContext) throws PluginException {
        JWaveZAgent agent = agent(builderContext);
        List<Emitter> emitters = buildEmitters(builderContext, agent);

        builderContext.getServices().getRegistrationService().registerStartUpTask(
            EmissionThread.ofRunnable(() -> agent.connect()));

        return BasicThing.builder()
            .emitters(emitters)
            .id(builderContext.getThingDTO().getId())
            .metadata(ThingMetadata.builder()
                .id(builderContext.getThingDTO().getId())
                .name(builderContext.getThingDTO().getName())
                .description(builderContext.getThingDTO().getName())
                .build())
            .build();
    }

    private JWaveZAgent agent(ThingBuilderContext builderContext) {
        return new JWaveZAgent(builderContext);
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
                return sceneActivationEmitterBuilder.buildEmitter(agent, context, emitterDTO);
            case EMITTER_TYPE_BASIC_SET:
                return basicSetEmitterBuilder.buildEmitter(agent, context, emitterDTO);
            default:
                throw new PluginException("Unknown emitter reference " + emitterDTO.getRef() + ", skipping this DTO");
        }
    }
}
