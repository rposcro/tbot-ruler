package com.tbot.ruler.controller.admin;

import java.util.List;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.persistance.model.ApplianceEntity;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.admin.AppliancesAdminService;
import com.tbot.ruler.service.admin.BindingsAdminService;
import com.tbot.ruler.service.admin.PluginsAdminService;
import com.tbot.ruler.service.admin.ThingsAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ThingEntity> getThingsEntities() {
        log.debug("Requested things entities ...");
    	return thingsService.allThings();
    }

    @GetMapping(path="/plugins", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PluginEntity> getPluginsEntities() {
        log.debug("Requested plugins entities ...");
        return pluginsService.allPlugins();
    }

    @GetMapping(path="/bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BindingEntity> getBindingsEntities() {
        log.debug("Requested bindings entities ...");
        return bindingsService.allBindings();
    }

    @GetMapping(path="/appliances", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ApplianceEntity> getAppliancesEntities() {
        log.debug("Requested appliances entities ...");
        return appliancesService.allAppliances();
    }
}