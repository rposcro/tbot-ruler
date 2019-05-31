package com.tbot.ruler.appliances;

import com.tbot.ruler.appliances.agents.ApplianceAgent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplianceClass {

    Class<? extends ApplianceAgent> agent();
}
