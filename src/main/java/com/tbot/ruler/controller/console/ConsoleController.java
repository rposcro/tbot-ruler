package com.tbot.ruler.controller.console;

import com.tbot.ruler.service.lifetime.BindingsLifetimeService;
import com.tbot.ruler.service.lifetime.SubjectLifetimeService;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.thing.Thing;
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
    private SubjectLifetimeService subjectLifetimeService;

    @Autowired
    private BindingsLifetimeService bindingsLifetimeService;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("plugins", subjectLifetimeService.getAllPlugins());
        mav.addObject("things", subjectLifetimeService.getAllThings());
        return mav;
    }

    @GetMapping(path = "things", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView things() {
        ModelAndView mav = new ModelAndView("things");
        mav.addObject("things", subjectLifetimeService.getAllThings());
        return mav;
    }

    @GetMapping(path = "things/{thingUuid}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView thingById(@PathVariable String thingUuid) {
        ModelAndView mav = new ModelAndView("things");
        Thing thing = subjectLifetimeService.getThingByUuid(thingUuid);
        mav.addObject("thingId", thingUuid);
        mav.addObject("thing", thing);
        mav.addObject("things", subjectLifetimeService.getAllThings());
        return mav;
    }

    @GetMapping(path = "actuators", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView actuators() {
        ModelAndView mav = new ModelAndView("actuators");
        mav.addObject("actuators", subjectLifetimeService.getAllActuators());
        return mav;
    }

    @GetMapping(path = "actuators/{actuatorId}", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView actuatorById(@PathVariable String actuatorId) {
        ModelAndView mav = new ModelAndView("actuators");
        Actuator actuator = subjectLifetimeService.getActuatorByUuid(actuatorId);
        mav.addObject("actuatorId", actuatorId);
        mav.addObject("actuator", actuator);
        mav.addObject("actuators", subjectLifetimeService.getAllActuators());
//        mav.addObject("senders", bindingsLifetimeService.getReceiversForSender(actuator.getUuid()));
//        mav.addObject("listeners", bindingsAdminService.receiversForSender(actuatorId));
        return mav;
    }

    @GetMapping(path = "about")
    public String about() {
        return "about";
    }
}
