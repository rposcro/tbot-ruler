package com.tbot.ruler.service.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class LifecycleStartupListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @Autowired
    private ThingsLifecycleService thingsLifecycleService;

    @Autowired
    private ActuatorsLifecycleService actuatorsLifecycleService;

    @Autowired
    private SubjectsTasksLifecycleService tasksLifecycleService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        pluginsLifecycleService.startUpAllPlugins();
        thingsLifecycleService.startUpAllThings();
        actuatorsLifecycleService.startUpAllActuators();
        tasksLifecycleService.startUpAllTasks();
    }
}
