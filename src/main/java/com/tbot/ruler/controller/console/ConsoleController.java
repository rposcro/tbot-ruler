package com.tbot.ruler.controller.console;

import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.service.admin.BindingsAdminService;
import com.tbot.ruler.service.admin.PluginsAdminService;
import com.tbot.ruler.service.admin.ThingsAdminService;
import com.tbot.ruler.things.builder.dto.ActuatorDTO;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.builder.dto.EmitterDTO;
import com.tbot.ruler.things.builder.dto.ThingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping(path = "/console")
public class ConsoleController {

    @Autowired
    private PluginsAdminService pluginsAdminService;

    @Autowired
    private ThingsAdminService thingsAdminService;

    @Autowired
    private AppliancesAdminService appliancesAdminService;

    @Autowired
    private BindingsAdminService bindingsAdminService;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("plugins", pluginsAdminService.allPlugins());
        mav.addObject("things", thingsAdminService.allThings());
        mav.addObject("thingsByPlugin", thingsAdminService.thingsByPlugin());
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        return mav;
    }

    @GetMapping(path = "things", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView things() {
        ModelAndView mav = new ModelAndView("things");
        mav.addObject("things", thingsAdminService.allThings());
        return mav;
    }

    @GetMapping(path = "things/{thingId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView thingById(@PathVariable String thingId) {
        ModelAndView mav = new ModelAndView("things");
        ThingDTO thingDTO = thingsAdminService.thingDTOById(thingId);
        mav.addObject("thingId", thingId);
        mav.addObject("thing", thingDTO);
        mav.addObject("things", thingsAdminService.allThings());
        mav.addObject("senders", bindingsAdminService.sendersForItem(thingId));
        mav.addObject("listeners", bindingsAdminService.listenersForItem(thingId));
        return mav;
    }

    @GetMapping(path = "emitters", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView emitters() {
        ModelAndView mav = new ModelAndView("emitters");
        mav.addObject("emitters", thingsAdminService.allEmitters());
        return mav;
    }

    @GetMapping(path = "emitters/{emitterId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView emitterById(@PathVariable String emitterId) {
        ModelAndView mav = new ModelAndView("emitters");
        EmitterDTO emitterDTO = thingsAdminService.emitterDTOById(emitterId);
        mav.addObject("emitterId", emitterId);
        mav.addObject("emitter", emitterDTO);
        mav.addObject("emitters", thingsAdminService.allEmitters());
        mav.addObject("senders", bindingsAdminService.sendersForItem(emitterId));
        mav.addObject("listeners", bindingsAdminService.listenersForItem(emitterId));
        return mav;
    }

    @GetMapping(path = "collectors", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView collectors() {
        ModelAndView mav = new ModelAndView("collectors");
        mav.addObject("collectors", thingsAdminService.allCollectors());
        return mav;
    }

    @GetMapping(path = "collectors/{collectorId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView collectorById(@PathVariable String collectorId) {
        ModelAndView mav = new ModelAndView("collectors");
        CollectorDTO collectorDTO = thingsAdminService.collectorDTOById(collectorId);
        mav.addObject("collectorId", collectorId);
        mav.addObject("collector", collectorDTO);
        mav.addObject("collectors", thingsAdminService.allCollectors());
        mav.addObject("senders", bindingsAdminService.sendersForItem(collectorId));
        mav.addObject("listeners", bindingsAdminService.listenersForItem(collectorId));
        return mav;
    }

    @GetMapping(path = "actuators", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView actuators() {
        ModelAndView mav = new ModelAndView("actuators");
        mav.addObject("actuators", thingsAdminService.allActuators());
        return mav;
    }

    @GetMapping(path = "actuators/{actuatorId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView actuatorById(@PathVariable String actuatorId) {
        ModelAndView mav = new ModelAndView("actuators");
        ActuatorDTO actuatorDTO = thingsAdminService.actuatorDTOById(actuatorId);
        mav.addObject("actuatorId", actuatorId);
        mav.addObject("actuator", actuatorDTO);
        mav.addObject("actuators", thingsAdminService.allActuators());
        mav.addObject("senders", bindingsAdminService.sendersForItem(actuatorId));
        mav.addObject("listeners", bindingsAdminService.listenersForItem(actuatorId));
        return mav;
    }

    @GetMapping(path = "appliances", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView appliances() {
        ModelAndView mav = new ModelAndView("appliances");
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        return mav;
    }

    @GetMapping(path = "appliances/{applianceId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView applianceById(@PathVariable String applianceId) {
        ModelAndView mav = new ModelAndView("appliances");
        ApplianceDTO applianceDTO = appliancesAdminService.applianceDTOById(applianceId);
        mav.addObject("applianceId", applianceId);
        mav.addObject("appliance", applianceDTO);
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        mav.addObject("senders", bindingsAdminService.sendersForItem(applianceId));
        mav.addObject("listeners", bindingsAdminService.listenersForItem(applianceId));
        return mav;
    }

    @GetMapping(path = "about")
    public String about() {
        return "about";
    }
}
