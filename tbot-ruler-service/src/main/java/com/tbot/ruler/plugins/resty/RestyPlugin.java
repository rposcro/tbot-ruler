package com.tbot.ruler.plugins.resty;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.ghost.GhostActuatorBuilder;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.instantiateActuatorsBuilders;

@Slf4j
public class RestyPlugin extends AbstractSubject implements Plugin {

    private final RulerPluginContext rulerPluginContext;
    private final Map<String, RestyActuatorBuilder> actuatorsBuilders;


    @Builder
    public RestyPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.actuatorsBuilders = instantiateActuatorsBuilders(
            RestyActuatorBuilder.class, "com.tbot.ruler.plugins.resty").stream()
            .collect(Collectors.toMap(RestyActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return buildActuator(actuatorEntity);
    }

    @Override
    public void stopActuator(Actuator actuator, String reference) {
        RestyActuatorBuilder builder = actuatorsBuilders.get(reference);
        if (builder == null) {
            throw new PluginException("Unknown builder reference " + reference);
        }
        builder.destroyActuator(actuator);
    }

    @Override
    public boolean hasJobs() {
        return super.hasJobs();
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity) {
        RestyActuatorBuilder actuatorBuilder = actuatorsBuilders.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            log.error("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, rulerPluginContext);
    }
}
