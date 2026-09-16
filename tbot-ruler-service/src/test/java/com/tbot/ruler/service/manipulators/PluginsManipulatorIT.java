package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.it.ActuatorsHelper;
import com.tbot.ruler.it.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.it.PluginsHelper;
import com.tbot.ruler.it.ThingsHelper;
import com.tbot.ruler.persistance.PluginsRepository;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PluginsManipulatorIT extends BaseIT {

	@Autowired
	private PluginsManipulator pluginsManipulator;

	@Autowired
	private PluginsHelper pluginsHelper;

	@Autowired
	private ActuatorsHelper actuatorsHelper;

	@Autowired
	private ThingsHelper thingsHelper;

	@Autowired
	private PluginsRepository pluginsRepository;

	@Test
	void removePlugin_whenNoActuators_deletesPlugin() {
		PluginEntity plugin = pluginsHelper.insertPlugin();

		pluginsManipulator.removePlugin(plugin);

		assertThat(pluginsRepository.findByUuid(plugin.getPluginUuid())).isEmpty();
	}

	@Test
	void removePlugin_whenActuatorsExist_throwsAndKeepsPlugin() {
		PluginEntity plugin = pluginsHelper.insertPlugin();
        ThingEntity thing = thingsHelper.insertThing();
        actuatorsHelper.insertActuator(plugin.getPluginId(), thing.getThingId());

		assertThatThrownBy(() -> pluginsManipulator.removePlugin(plugin))
			.isInstanceOf(LifecycleException.class)
			.hasMessageContaining("Cannot remove plugin");

		assertThat(pluginsRepository.findByUuid(plugin.getPluginUuid())).isPresent();
	}
}

