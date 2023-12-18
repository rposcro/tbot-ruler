package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.subjects.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class LifecycleStartupListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @Autowired
    private ThingsLifecycleService thingsLifecycleService;

    @Autowired
    private ActuatorsLifecycleService actuatorsLifecycleService;

    @Autowired
    private JobsLifecycleService jobsLifecycleService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        pluginsLifecycleService.startUpAllPlugins();
        thingsLifecycleService.startUpAllThings();
        actuatorsLifecycleService.activateAllActuators();

        subjectsWithJobs().forEach(jobsLifecycleService::startSubjectJobs);
    }

    private List<Subject> subjectsWithJobs() {
        LinkedList<Subject> subjects = new LinkedList<>();
        subjects.addAll(pluginsLifecycleService.getAllPlugins());
        subjects.addAll(thingsLifecycleService.getAllThings());
        subjects.addAll(actuatorsLifecycleService.getAllActuators());

        return subjects.stream()
                .filter(subject -> !subject.getJobBundles().isEmpty())
                .toList();
    }
}
