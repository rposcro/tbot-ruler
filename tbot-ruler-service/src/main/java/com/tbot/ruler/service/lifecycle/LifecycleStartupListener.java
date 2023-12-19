package com.tbot.ruler.service.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class LifecycleStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @Autowired
    private ThingsLifecycleService thingsLifecycleService;

    @Autowired
    private ActuatorsLifecycleService actuatorsLifecycleService;

    @Autowired
    private BindingsLifecycleService bindingsLifecycleService;

    @Autowired
    private BrokerLifecycleService brokerLifecycleService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        pluginsLifecycleService.activateAllPlugins();
        thingsLifecycleService.activateAllThings();
        actuatorsLifecycleService.activateAllActuators();
        bindingsLifecycleService.reloadCache();
        brokerLifecycleService.startBroker();
    }
}
