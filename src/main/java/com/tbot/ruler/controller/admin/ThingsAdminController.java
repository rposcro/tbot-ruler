package com.tbot.ruler.controller.admin;

import java.util.List;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.service.admin.BindingsAdminService;
import com.tbot.ruler.service.admin.PluginsAdminService;
import com.tbot.ruler.service.admin.ThingsAdminService;
import com.tbot.ruler.things.builder.dto.ApplianceDTO;
import com.tbot.ruler.things.builder.dto.BindingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tbot.ruler.things.builder.dto.ThingDTO;
import com.tbot.ruler.things.builder.dto.ThingPluginDTO;

@Slf4j
@RestController
@RequestMapping(path = "/common")
public class ThingsAdminController extends AbstractController {

    @Autowired
    private PluginsAdminService pluginsService;
    @Autowired
    private ThingsAdminService thingsService;
    @Autowired
    private BindingsAdminService bindingsService;
    @Autowired
    private AppliancesAdminService appliancesService;

    @GetMapping(path="/things", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ThingDTO> getThingsDTOs() {
        log.debug("Requested things dto ...");
    	return thingsService.allThings();
    }

    @GetMapping(path="/plugins", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ThingPluginDTO> getPluginsDTOs() {
        log.debug("Requested plugins dto ...");
        return pluginsService.allPlugins();
    }

    @GetMapping(path="/bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BindingDTO> getBindingsDTOs() {
        log.debug("Requested bindings dto ...");
        return bindingsService.allBindings();
    }

    @GetMapping(path="/appliances", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ApplianceDTO> getAppliancesDTOs() {
        log.debug("Requested appliances dto ...");
        return appliancesService.allAppliances();
    }
}