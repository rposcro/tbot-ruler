package com.tbot.ruler.controller.console;

import com.tbot.ruler.controller.console.payload.BindingReference;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(path = "/console")
public class ConsoleController {

    @Autowired
    private PluginsRepository pluginsRepository;

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("plugins", pluginsRepository.findAll());
        mav.addObject("things", thingsRepository.findAll());
        mav.addObject("actuators", actuatorsRepository.findAll());
        return mav;
    }

    @GetMapping(path = "plugins", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView plugins() {
        ModelAndView mav = new ModelAndView("plugins");
        mav.addObject("plugins", pluginsRepository.findAll());
        return mav;
    }

    @GetMapping(path = "plugins/{pluginUuid}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView pluginById(@PathVariable String pluginUuid) {
        ModelAndView mav = new ModelAndView("plugins");
        PluginEntity pluginEntity = pluginsRepository.findByUuid(pluginUuid).orElse(null);
        mav.addObject("pluginUuid", pluginUuid);
        mav.addObject("plugin", pluginEntity);
        mav.addObject("plugins", pluginsRepository.findAll());
        return mav;
    }

    @GetMapping(path = "things", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView things() {
        ModelAndView mav = new ModelAndView("things");
        mav.addObject("things", thingsRepository.findAll());
        return mav;
    }

    @GetMapping(path = "things/{thingUuid}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView thingById(@PathVariable String thingUuid) {
        ModelAndView mav = new ModelAndView("things");
        ThingEntity thingEntity = thingsRepository.findByUuid(thingUuid).orElse(null);
        mav.addObject("thingUuid", thingUuid);
        mav.addObject("thing", thingEntity);
        mav.addObject("things", thingsRepository.findAll());
        mav.addObject("actuators", actuatorsRepository.findByThingId(thingEntity.getThingId()));
        return mav;
    }

    @GetMapping(path = "actuators", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView actuators() {
        ModelAndView mav = new ModelAndView("actuators");
        mav.addObject("actuators", actuatorsRepository.findAll());
        return mav;
    }

    @GetMapping(path = "actuators/{actuatorUuid}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView actuatorById(@PathVariable String actuatorUuid) {
        ModelAndView mav = new ModelAndView("actuators");
        ActuatorEntity actuatorEntity = actuatorsRepository.findByUuid(actuatorUuid).orElse(null);
        mav.addObject("actuatorUuid", actuatorUuid);
        mav.addObject("actuator", actuatorEntity);
        mav.addObject("actuators", actuatorsRepository.findAll());
        mav.addObject("plugin", pluginsRepository.findById(actuatorEntity.getPluginId()).orElse(null));
        mav.addObject("thing", thingsRepository.findById(actuatorEntity.getThingId()).orElse(null));
        mav.addObject("inboundBindings", toSendersReferences(bindingsRepository.findByReceiverUuid(actuatorUuid)));
        mav.addObject("outboundBindings", toReceiversReferences(bindingsRepository.findBySenderUuid(actuatorUuid)));
        return mav;
    }

    @GetMapping(path = "bindings", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView bindings() {
        ModelAndView mav = new ModelAndView("bindings");
        List<BindingEntity> bindings = bindingsRepository.findAll();
        List<String> senders = bindings.stream()
                .map(BindingEntity::getSenderUuid)
                .sorted()
                .toList();
        Map<String, Set<String>> bindingsMap = bindings.stream()
                .collect(Collectors.groupingBy(
                        BindingEntity::getSenderUuid,
                        Collectors.mapping(BindingEntity::getReceiverUuid, Collectors.<String>toSet())));

        mav.addObject("senders", senders);
        mav.addObject("bindingsMap", bindingsMap);
        return mav;
    }

    private List<BindingReference> toSendersReferences(List<BindingEntity> bindingEntities) {
        return bindingEntities.stream()
                .map(entity -> BindingReference.builder()
                        .uuid(entity.getSenderUuid())
                        .actuatorUuid(actuatorsRepository.findByUuid(entity.getSenderUuid()).isPresent())
                        .build())
                .toList();
    }

    private List<BindingReference> toReceiversReferences(List<BindingEntity> bindingEntities) {
        return bindingEntities.stream()
                .map(entity -> BindingReference.builder()
                        .uuid(entity.getReceiverUuid())
                        .actuatorUuid(actuatorsRepository.findByUuid(entity.getReceiverUuid()).isPresent())
                        .build())
                .toList();
    }
}
