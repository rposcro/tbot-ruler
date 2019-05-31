package com.tbot.ruler.configuration;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.ApplianceClass;
import com.tbot.ruler.appliances.agents.ApplianceAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AgentsConfiguration {

    @Bean
    public Map<ApplianceClass, ApplianceAgent> agentsPerApplianceClass(Map<String, Class<? extends Appliance>> applianceClassesMap) {
        return applianceClassesMap.values().stream()
            .map(clazz -> clazz.getAnnotationsByType(ApplianceClass.class)[0])
            .collect(Collectors.toMap(Function.identity(), ann -> constructAgent(ann.agent())));
    }

    private ApplianceAgent constructAgent(Class<? extends ApplianceAgent> agentClass) {
        try {
            return agentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot initialize application configuration!", e);
        }
    }
}
