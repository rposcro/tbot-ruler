package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.persistance.model.PluginEntity;
import com.tbot.ruler.persistance.model.ThingEntity;
import com.tbot.ruler.service.lifecycle.ActuatorsLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActuatorsManipulatorIT extends BaseIT {

    @Autowired
    private ActuatorsManipulator actuatorsManipulator;

    @MockBean
    private ActuatorsLifecycleService actuatorsLifecycleService;

    @Test
    void createActuator_createsActuator() {
        ActuatorEntity actuatorEntity = newActuatorEntity();
        ActuatorEntity createdActuator = actuatorsManipulator.createActuator(actuatorEntity);

        assertThat(createdActuator.getActuatorId()).isPositive();
        assertThat(createdActuator.getActuatorUuid()).isEqualTo(actuatorEntity.getActuatorUuid());

        ActuatorEntity persistedActuator = actuatorsRepository.findByUuid(actuatorEntity.getActuatorUuid()).orElseThrow();
        assertThat(persistedActuator.getActuatorId()).isEqualTo(createdActuator.getActuatorId());
        assertThat(persistedActuator.getThingId()).isEqualTo(actuatorEntity.getThingId());
        assertThat(persistedActuator.getPluginId()).isEqualTo(actuatorEntity.getPluginId());
        assertThat(persistedActuator.getReference()).isEqualTo(actuatorEntity.getReference());
        assertThat(persistedActuator.getName()).isEqualTo(actuatorEntity.getName());
        assertThat(persistedActuator.getDescription()).isEqualTo(actuatorEntity.getDescription());
        assertThat(persistedActuator.getConfiguration()).isEqualTo(actuatorEntity.getConfiguration());
    }

    @Test
    void createActuator_whenActivateThrows_doesNotPersistActuator() {
        ActuatorEntity actuatorEntity = newActuatorEntity();
        doThrow(new RuntimeException("activation failed")).when(actuatorsLifecycleService).activateActuator(any(ActuatorEntity.class));

        assertThatThrownBy(() -> actuatorsManipulator.createActuator(actuatorEntity))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("activation failed");

        assertThat(actuatorsRepository.findByUuid(actuatorEntity.getActuatorUuid())).isEmpty();
    }

    @Test
    void removeActuator_whenNoBindings_deletesActuator() {
        ActuatorEntity persistedActuator = actuatorsRepository.save(newActuatorEntity());
        when(actuatorsLifecycleService.isActuatorActive(persistedActuator.getActuatorUuid())).thenReturn(false);

        actuatorsManipulator.removeActuator(persistedActuator);

        assertThat(actuatorsRepository.findByUuid(persistedActuator.getActuatorUuid())).isEmpty();
    }

    @Test
    void removeActuator_whenBindingsExist_throwsAndKeepsActuator() {
        ActuatorEntity persistedActuator = actuatorsRepository.save(newActuatorEntity());
        insertSenderBinding(persistedActuator.getActuatorUuid());

        assertThatThrownBy(() -> actuatorsManipulator.removeActuator(persistedActuator))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("Cannot remove actuator");

        assertThat(actuatorsRepository.findByUuid(persistedActuator.getActuatorUuid())).isPresent();
    }

    @Test
    void removeActuator_whenActive_deactivatesAndDeletesActuator() {
        ActuatorEntity persistedActuator = actuatorsRepository.save(newActuatorEntity());
        when(actuatorsLifecycleService.isActuatorActive(persistedActuator.getActuatorUuid())).thenReturn(true);

        actuatorsManipulator.removeActuator(persistedActuator);

        verify(actuatorsLifecycleService).deactivateActuator(persistedActuator);
        assertThat(actuatorsRepository.findByUuid(persistedActuator.getActuatorUuid())).isEmpty();
    }

    private ActuatorEntity newActuatorEntity() {
        ThingEntity thing = thingsRepository.save(newThingEntity());
        PluginEntity plugin = pluginsRepository.save(newPluginEntity());
        return newActuatorEntity(plugin.getPluginId(), thing.getThingId());
    }
}
