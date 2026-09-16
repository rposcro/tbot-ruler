package com.tbot.ruler.plugins.cron;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.plugin.Plugin;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CronPlugin extends AbstractSubject implements Plugin {

    private static final String ACTUATOR_REFERENCE = "cron";
    private static final CronActuatorBuilder ACTUATOR_BUILDER = new CronActuatorBuilder();

    @Builder
    public CronPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        if (!ACTUATOR_REFERENCE.equals(actuatorEntity.getReference())) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference()
                    + ", supported references: " + getSupportedActuatorReferences());
        }
        return ACTUATOR_BUILDER.buildActuator(rulerThingContext, actuatorEntity);
    }

    @Override
    public List<String> getSupportedActuatorReferences() {
        return List.of(ACTUATOR_REFERENCE);
    }
}

