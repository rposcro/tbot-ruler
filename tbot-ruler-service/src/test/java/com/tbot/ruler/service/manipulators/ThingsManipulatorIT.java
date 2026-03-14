package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.lifecycle.ThingsLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;

class ThingsManipulatorIT extends BaseIT {

    @Autowired
    private ThingsManipulator thingsManipulator;

    @SpyBean
    private ThingsLifecycleService thingsLifecycleService;

    @Test
    void createThing_createsThing() {
        ThingEntity thingEntity = newThingEntity();

        doCallRealMethod().when(thingsLifecycleService).activateThing(any(ThingEntity.class));

        ThingEntity createdThing = thingsManipulator.createThing(thingEntity);

        assertThat(createdThing.getThingId()).isPositive();
        assertThat(createdThing.getThingUuid()).isEqualTo(thingEntity.getThingUuid());

        ThingEntity persistedThing = thingsRepository.findByUuid(thingEntity.getThingUuid()).orElseThrow();
        assertThat(persistedThing.getThingId()).isEqualTo(createdThing.getThingId());
        assertThat(persistedThing.getName()).isEqualTo(thingEntity.getName());
        assertThat(persistedThing.getDescription()).isEqualTo(thingEntity.getDescription());
        assertThat(persistedThing.getConfiguration()).isEqualTo(thingEntity.getConfiguration());
    }

    @Test
    void createThing_whenActivateThingThrows_doesNotPersistThing() {
        ThingEntity thingEntity = newThingEntity();

        doThrow(new RuntimeException("activation failed")).when(thingsLifecycleService).activateThing(any(ThingEntity.class));

        assertThatThrownBy(() -> thingsManipulator.createThing(thingEntity))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("activation failed");

        assertThat(thingsRepository.findByUuid(thingEntity.getThingUuid())).isEmpty();
    }

    @Test
    void removeThing_whenNoActuators_deletesThing() {
        ThingEntity persistedThing = insertThing();

        thingsManipulator.removeThing(persistedThing);

        assertThat(thingsRepository.findByUuid(persistedThing.getThingUuid())).isEmpty();
    }

    @Test
    void removeThing_whenActuatorsExist_throwsAndKeepsThing() {
        PluginEntity persistedPlugin = insertPlugin();
        ThingEntity persistedThing = insertThing();
        insertActuator(persistedPlugin.getPluginId(), persistedThing.getThingId());

        assertThatThrownBy(() -> thingsManipulator.removeThing(persistedThing))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("Cannot remove thing");

        assertThat(thingsRepository.findByUuid(persistedThing.getThingUuid())).isPresent();
    }
}
