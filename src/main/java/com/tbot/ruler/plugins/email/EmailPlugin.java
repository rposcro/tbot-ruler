package com.tbot.ruler.plugins.email;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.Plugin;
import com.tbot.ruler.plugins.RulerPluginContext;
import com.tbot.ruler.plugins.PluginsUtil;
import com.tbot.ruler.subjects.AbstractSubject;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.RulerThingContext;
import lombok.Builder;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tbot.ruler.plugins.PluginsUtil.parseConfiguration;

public class EmailPlugin extends AbstractSubject implements Plugin {

    private final RulerPluginContext rulerPluginContext;
    private final Map<String, EmailActuatorBuilder> buildersMap;

    @Builder
    public EmailPlugin(RulerPluginContext rulerPluginContext) {
        super(rulerPluginContext.getPluginUuid(), rulerPluginContext.getPluginName());
        this.rulerPluginContext = rulerPluginContext;
        this.buildersMap = PluginsUtil.instantiateActuatorsBuilders(EmailActuatorBuilder.class, "com.tbot.ruler.plugins.email").stream()
                .collect(Collectors.toMap(EmailActuatorBuilder::getReference, Function.identity()));
    }

    @Override
    public Actuator startUpActuator(ActuatorEntity actuatorEntity, RulerThingContext rulerThingContext) {
        return buildActuator(actuatorEntity);
    }

    private Actuator buildActuator(ActuatorEntity actuatorEntity) {
        EmailActuatorBuilder actuatorBuilder = buildersMap.get(actuatorEntity.getReference());
        if (actuatorBuilder == null) {
            throw new PluginException("Unknown actuator reference " + actuatorEntity.getReference() + ", skipping this entity");
        }
        EmailSenderConfiguration senderConfiguration = parseConfiguration(
                rulerPluginContext.getPluginConfiguration(), EmailSenderConfiguration.class);

        return actuatorBuilder.buildActuator(
                actuatorEntity,
                rulerPluginContext,
                senderConfiguration);
    }
}
