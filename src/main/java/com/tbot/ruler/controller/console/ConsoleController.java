package com.tbot.ruler.controller.console;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.controller.ControllerConstants;
import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.service.admin.BindingsAdminService;
import com.tbot.ruler.service.admin.PluginsAdminService;
import com.tbot.ruler.service.admin.ThingsAdminService;
import com.tbot.ruler.things.ThingId;
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

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(path = ControllerConstants.ENDPOINT_CONSOLE)
public class ConsoleController {

    @Autowired
    private PluginsAdminService pluginsService;

    @Autowired
    private ThingsAdminService thingsService;

    @Autowired
    private AppliancesAdminService appliancesService;

    @Autowired
    private BindingsAdminService bindingsService;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("plugins", pluginsService.allPlugins());
        mav.addObject("thingsByPlugin", thingsService.thingsByPlugin());
        mav.addObject("appliances", appliancesService.allAppliances());
        return mav;
    }

    @GetMapping(path = "things", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView things() {
        ModelAndView mav = new ModelAndView("things");
        mav.addObject("things", thingsService.allThings());
        return mav;
    }

    @GetMapping(path = "things/{thingId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView things(@PathVariable ThingId thingId) {
        ModelAndView mav = new ModelAndView("things");
        mav.addObject("things", thingsService.allThings());

        ThingDTO thingDTO = thingsService.thingDTOById(thingId);
        mav.addObject("thingId", thingId);
        mav.addObject("thing", thingDTO);
        mav.addObject("appliancesPerEmitter", thingDTO.getEmitters().stream()
            .map(EmitterDTO::getId)
            .collect(Collectors.toMap(emitterId -> emitterId, emitterId -> bindingsService.appliancesByEmitter(emitterId))));
        mav.addObject("appliancesPerCollector", thingDTO.getCollectors().stream()
            .map(CollectorDTO::getId)
            .collect(Collectors.toMap(collectorId -> collectorId, collectorId -> bindingsService.appliancesByCollector(collectorId))));
        mav.addObject("appliancesPerActuator", thingDTO.getActuators().stream()
            .map(ActuatorDTO::getId)
            .collect(Collectors.toMap(actuatorId -> actuatorId, actuatorId -> bindingsService.appliancesByActuator(actuatorId))));
        return mav;
    }

    @GetMapping(path = "appliances", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView appliances() {
        ModelAndView mav = new ModelAndView("appliances");
        mav.addObject("appliances", appliancesService.allAppliances());
        return mav;
    }

    @GetMapping(path = "appliances/{applianceId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView appliances(@PathVariable ApplianceId applianceId) {
        ModelAndView mav = new ModelAndView("appliances");
        mav.addObject("appliances", appliancesService.allAppliances());

        ApplianceDTO applianceDTO = appliancesService.applianceDTOById(applianceId);
        mav.addObject("applianceId", applianceId);
        mav.addObject("appliance", applianceDTO);
        mav.addObject("bindedEmitters", bindingsService.bindedEmittersByAppliance(applianceId));
        mav.addObject("bindedCollectors", bindingsService.bindedCollectorsByAppliance(applianceId));
        mav.addObject("bindedActuator", bindingsService.bindedActuatorByAppliance(applianceId));
        return mav;
    }
}
