package com.tbot.ruler.controller.console;

import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.service.admin.BindingsAdminService;
import com.tbot.ruler.service.admin.PluginsAdminService;
import com.tbot.ruler.service.admin.ThingsAdminService;
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
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        return mav;
    }

    @GetMapping(path = "things", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView things() {
        ModelAndView mav = new ModelAndView("things");
        mav.addObject("things", thingsAdminService.allThings());
        return mav;
    }

    @GetMapping(path = "things/{thingUuid}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView thingById(@PathVariable String thingUuid) {
        ModelAndView mav = new ModelAndView("things");
        ThingEntity thingEntity = thingsAdminService.thingByUuid(thingUuid);
        mav.addObject("thingId", thingUuid);
        mav.addObject("thing", thingEntity);
        mav.addObject("things", thingsAdminService.allThings());
        mav.addObject("senders", bindingsAdminService.sendersForItem(thingUuid));
        mav.addObject("listeners", bindingsAdminService.receiversForItem(thingUuid));
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
        ActuatorEntity actuatorEntity = thingsAdminService.actuatorByUuid(actuatorId);
        mav.addObject("actuatorId", actuatorId);
        mav.addObject("actuator", actuatorEntity);
        mav.addObject("actuators", thingsAdminService.allActuators());
        mav.addObject("senders", bindingsAdminService.sendersForItem(actuatorId));
        mav.addObject("listeners", bindingsAdminService.receiversForItem(actuatorId));
        return mav;
    }

    @GetMapping(path = "appliances", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView appliances() {
        ModelAndView mav = new ModelAndView("appliances");
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        return mav;
    }

    @GetMapping(path = "appliances/{applianceUuid}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView applianceById(@PathVariable String applianceUuid) {
        ModelAndView mav = new ModelAndView("appliances");
        ApplianceEntity applianceEntity = appliancesAdminService.applianceByUuid(applianceUuid);
        mav.addObject("applianceId", applianceUuid);
        mav.addObject("appliance", applianceEntity);
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        mav.addObject("senders", bindingsAdminService.sendersForItem(applianceUuid));
        mav.addObject("listeners", bindingsAdminService.receiversForItem(applianceUuid));
        return mav;
    }

    @GetMapping(path = "about")
    public String about() {
        return "about";
    }
}
