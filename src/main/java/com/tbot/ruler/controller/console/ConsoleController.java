package com.tbot.ruler.controller.console;

import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.service.admin.BindingsAdminService;
import com.tbot.ruler.service.admin.PluginsAdminService;
import com.tbot.ruler.service.admin.ThingsAdminService;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
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

//    @GetMapping(path = "things/{thingId}", produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView things(@PathVariable ItemId thingId) {
//        ModelAndView mav = new ModelAndView("things");
//        mav.addObject("things", thingsService.allThings());
//
//        ThingDTO thingDTO = thingsService.thingDTOById(thingId);
//        mav.addObject("thingId", thingId);
//        mav.addObject("thing", thingDTO);
//        mav.addObject("appliancesPerEmitter", thingDTO.getEmitters().stream()
//            .map(EmitterDTO::getId)
//            .collect(Collectors.toMap(emitterId -> emitterId, emitterId -> bindingsService.appliancesByEmitter(emitterId))));
//        mav.addObject("appliancesPerCollector", thingDTO.getCollectors().stream()
//            .map(CollectorDTO::getId)
//            .collect(Collectors.toMap(collectorId -> collectorId, collectorId -> bindingsService.appliancesByCollector(collectorId))));
//        mav.addObject("appliancesPerActuator", thingDTO.getActuators().stream()
//            .map(ActuatorDTO::getId)
//            .collect(Collectors.toMap(actuatorId -> actuatorId, actuatorId -> bindingsService.appliancesByActuator(actuatorId))));
//        return mav;
//    }

    @GetMapping(path = "appliances", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView appliances() {
        ModelAndView mav = new ModelAndView("appliances");
        mav.addObject("appliances", appliancesAdminService.allAppliances());
        return mav;
    }

    @GetMapping(path = "appliances/{applianceId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView appliances(@PathVariable String applianceId) {
        ModelAndView mav = new ModelAndView("appliances");

        ApplianceDTO applianceDTO = appliancesAdminService.applianceDTOById(applianceId);
        mav.addObject("applianceId", applianceId);
        mav.addObject("appliance", applianceDTO);
        mav.addObject("senders", bindingsAdminService.sendersForItem(applianceId));
        mav.addObject("listeners", bindingsAdminService.listenersForItem(applianceId));
        return mav;
    }

    @GetMapping(path = "about")
    public String about() {
        return "about";
    }
}
