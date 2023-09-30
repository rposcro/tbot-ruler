package com.tbot.ruler.plugins.email;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.plugins.PluginBuilderContext;
import com.tbot.ruler.subjects.Actuator;
import lombok.Getter;

public abstract class EmailActuatorBuilder {

    @Getter
    protected String reference;

    protected EmailActuatorBuilder(String reference) {
        this.reference = reference;
    }

    public abstract Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            PluginBuilderContext pluginBuilderContext,
            EmailSenderConfiguration emailSenderConfiguration) throws PluginException;
}
