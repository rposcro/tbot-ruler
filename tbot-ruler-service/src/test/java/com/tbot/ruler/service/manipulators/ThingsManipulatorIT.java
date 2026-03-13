package com.tbot.ruler.service.manipulators;

import com.fasterxml.jackson.databind.node.TextNode;
import com.tbot.ruler.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.lifecycle.ThingsLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ThingsManipulatorIT extends BaseIT {

    @Autowired
    private ThingsManipulator thingsManipulator;

    @Autowired
    private ThingsRepository thingsRepository;

    @MockBean
    private ActuatorsRepository actuatorsRepository;

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
        ThingEntity persistedThing = thingsRepository.save(newThingEntity());
        when(actuatorsRepository.actuatorsForThingExist(persistedThing.getThingId())).thenReturn(false);

        thingsManipulator.removeThing(persistedThing);

        assertThat(thingsRepository.findByUuid(persistedThing.getThingUuid())).isEmpty();
    }

    @Test
    void removeThing_whenActuatorsExist_throwsAndKeepsThing() {
        ThingEntity persistedThing = thingsRepository.save(newThingEntity());
        when(actuatorsRepository.actuatorsForThingExist(persistedThing.getThingId())).thenReturn(true);

        assertThatThrownBy(() -> thingsManipulator.removeThing(persistedThing))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("Cannot remove thing");

        assertThat(thingsRepository.findByUuid(persistedThing.getThingUuid())).isPresent();
    }

    private ThingEntity newThingEntity() {
        return ThingEntity.builder()
            .thingUuid("thng-" + UUID.randomUUID())
            .name("Thing Name")
            .description("Some thing description")
            .configuration(new TextNode("property"))
            .build();
    }
}
