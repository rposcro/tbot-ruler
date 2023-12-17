package com.tbot.ruler.plugins.agent;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.PluginsUtil;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AgentPlugin extends AbstractSubject implements Plugin {

    private final RulerPluginContext rulerPluginContext;
    private final Map<String, AgentActuatorBuilder> buildersMap;

    @Builder
    public AgentPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(AgentActuatorBuilder.class, "com.tbot.ruler.plugins.agent").stream()
                .collect(Collectors.toMap(AgentActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return buildActuator(actuatorEntity, rulerThingContext);
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        AgentActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }

        return actuatorBuilder.buildActuator(
                actuatorEntity,
                rulerThingContext);
    }
}
