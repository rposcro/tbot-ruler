package com.tbot.ruler.service;

import com.tbot.ruler.subjects.plugin.PluginFactory;
import com.tbot.ruler.util.PackageScanner;
import lombok.Getter;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StructureService {

    private PackageScanner packageScanner = new PackageScanner();

    @Getter
    private List<Class<? extends PluginFactory>> pluginsFactories;

    @EventListener
    public void initialize(ApplicationStartedEvent event) {
        initializePluginsFactories();
    }

    private void initializePluginsFactories() {
        pluginsFactories = packageScanner.findAllClassesOfType(PluginFactory.class).stream()
                .sorted(Comparator.comparing(Class::getName))
                .toList();
    }
}
