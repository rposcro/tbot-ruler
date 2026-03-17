package com.tbot.ruler.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.BaseIT;
import com.tbot.ruler.controller.admin.payload.PluginCreateRequest;
import com.tbot.ruler.service.lifecycle.PluginsLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PluginsAdminControllerIT extends BaseIT {

    private static final String FACTORY_CLASS = "com.tbot.ruler.plugins.cron.CronPluginFactory";
    private static final String FAKE_FACTORY_CLASS = "com.tbot.ruler.plugins.fake.FakePluginFactory";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PluginsLifecycleService pluginsLifecycleService;

    @Test
    void createPlugin_returns200AndPersistsPlugin() throws Exception {

        PluginCreateRequest request = PluginCreateRequest.builder()
            .name("Cron Plugin")
            .factoryClass(FACTORY_CLASS)
            .configuration(new TextNode("plugin-config"))
            .build();

        mockMvc.perform(post("/admin/plugins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Cron Plugin"))
            .andExpect(jsonPath("$.factoryClass").value(FACTORY_CLASS))
            .andExpect(jsonPath("$.pluginUuid").isNotEmpty())
            .andExpect(jsonPath("$.supportedActuatorReferences[0]").value("cron"));

        assertThat(pluginsRepository.findAll())
            .anyMatch(p -> p.getName().equals("Cron Plugin") && p.getFactoryClass().equals(FACTORY_CLASS));
    }

    @Test
    void createPlugin_whenActivationReturnsNull_returns500() throws Exception {
        PluginCreateRequest request = PluginCreateRequest.builder()
            .name("Bad Plugin")
            .factoryClass(FAKE_FACTORY_CLASS)
            .configuration(null)
            .build();

        mockMvc.perform(post("/admin/plugins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());
    }
}

