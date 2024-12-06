package com.tbot.ruler.plugins.resty.sender;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.resty.RestyActuatorBuilder;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;

public class RestySenderActuatorBuilder extends RestyActuatorBuilder {

    public RestySenderActuatorBuilder() {
        super("sender");
    }

    @Override
    public Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            RulerPluginContext rulerPluginContext) throws PluginException {
        RestySenderConfiguration restyConfiguration = rulerPluginContext.getPluginConfigurationDeserializer()
            .parseConfiguration(actuatorEntity.getConfiguration(), RestySenderConfiguration.class);
        return RestySenderActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .restySenderConfiguration(restyConfiguration)
                .build();
    }
}
