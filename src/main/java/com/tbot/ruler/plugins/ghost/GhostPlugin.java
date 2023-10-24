package com.tbot.ruler.plugins.ghost;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

@Getter
public class GhostPlugin extends AbstractSubject implements Plugin {

    private final static Map<String, GhostActuatorBuilder> ACTUATORS_BUILDERS =
            PluginsUtil.instantiateActuatorsBuilders(GhostActuatorBuilder.class, "com.tbot.ruler.plugins.ghost").stream()
                .collect(Collectors.toMap(GhostActuatorBuilder::getReference, Function.identity()));

    private final RulerPluginContext rulerPluginContext;
    private final GhostPluginContext ghostPluginContext;

    @Builder
    public GhostPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.ghostPluginContext = GhostPluginContext.builder()
                .rulerPluginContext(rulerPluginContext)
                .ghostPluginConfiguration(parseConfiguration(
                        rulerPluginContext.getPluginConfiguration(), GhostPluginConfiguration.class))
                .build();
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return buildActuator(actuatorEntity, rulerThingContext, ghostPluginContext);
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext, GhostPluginContext ghostPluginContext) {
        GhostActuatorBuilder actuatorBuilder = ACTUATORS_BUILDERS.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        return actuatorBuilder.buildActuator(actuatorEntity, rulerThingContext, ghostPluginContext);
    }
}
