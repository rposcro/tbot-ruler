package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PluginsManipulatorIT extends BaseIT {

	@Autowired
	private PluginsManipulator pluginsManipulator;

	@Test
	void removePlugin_whenNoActuators_deletesPlugin() {
		PluginEntity plugin = pluginsRepository.save(newPluginEntity());

		pluginsManipulator.removePlugin(plugin);

		assertThat(pluginsRepository.findByUuid(plugin.getPluginUuid())).isEmpty();
	}

	@Test
	void removePlugin_whenActuatorsExist_throwsAndKeepsPlugin() {
		PluginEntity plugin = insertPlugin();
        ThingEntity thing = insertThing();
        insertActuator(plugin.getPluginId(), thing.getThingId());

		assertThatThrownBy(() -> pluginsManipulator.removePlugin(plugin))
			.isInstanceOf(LifecycleException.class)
			.hasMessageContaining("Cannot remove plugin");

		assertThat(pluginsRepository.findByUuid(plugin.getPluginUuid())).isPresent();
	}
}

