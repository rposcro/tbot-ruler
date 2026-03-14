package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.BaseIT;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.model.BindingEntity;
import com.tbot.ruler.service.lifecycle.BindingsLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BindingsManipulatorIT extends BaseIT {

    @Autowired
    private BindingsManipulator bindingsManipulator;

    @MockBean
    private BindingsLifecycleService bindingsLifecycleService;

    @Test
    void addBinding_addsBindingAndReloadsCache() {
        String senderUuid = "snd-" + UUID.randomUUID();
        String receiverUuid = "rcv-" + UUID.randomUUID();

        bindingsManipulator.addBinding(senderUuid, receiverUuid);

        assertThat(bindingsRepository.find(senderUuid, receiverUuid)).isPresent();
        // Verify that reloadCache is called twice: once during application startup and once after adding the binding
        verify(bindingsLifecycleService, times(2)).reloadCache();
    }

    @Test
    void addBinding_whenAlreadyExists_throwsAndDoesNotReloadCache() {
        String senderUuid = "snd-" + UUID.randomUUID();
        String receiverUuid = "rcv-" + UUID.randomUUID();
        insertBinding(senderUuid, receiverUuid);

        assertThatThrownBy(() -> bindingsManipulator.addBinding(senderUuid, receiverUuid))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("already exists");

        verify(bindingsLifecycleService, never()).reloadCache();
    }

    @Test
    void addBinding_whenInsertFails_throwsAndDoesNotPersistBinding() {
        String senderUuid = "snd-" + UUID.randomUUID();
        String receiverUuid = "rcv-" + UUID.randomUUID();
        doReturn(false).when(bindingsRepository).insert(any(BindingEntity.class));

        assertThatThrownBy(() -> bindingsManipulator.addBinding(senderUuid, receiverUuid))
            .isInstanceOf(LifecycleException.class)
            .hasMessageContaining("Failed to add binding");

        assertThat(bindingsRepository.find(senderUuid, receiverUuid)).isEmpty();
        verify(bindingsLifecycleService, never()).reloadCache();
    }

    @Test
    void removeBinding_deletesBindingAndReloadsCache() {
        String senderUuid = "snd-" + UUID.randomUUID();
        String receiverUuid = "rcv-" + UUID.randomUUID();
        BindingEntity binding = insertBinding(senderUuid, receiverUuid);

        bindingsManipulator.removeBinding(binding);

        assertThat(bindingsRepository.find(senderUuid, receiverUuid)).isEmpty();
        verify(bindingsLifecycleService).reloadCache();
    }

    @Test
    void removeBinding_whenCacheReloadFails_keepsBindingInDb() {
        String senderUuid = "snd-" + UUID.randomUUID();
        String receiverUuid = "rcv-" + UUID.randomUUID();
        BindingEntity binding = insertBinding(senderUuid, receiverUuid);
        doThrow(new RuntimeException("cache reload failed")).when(bindingsLifecycleService).reloadCache();

        assertThatThrownBy(() -> bindingsManipulator.removeBinding(binding))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("cache reload failed");

        assertThat(bindingsRepository.find(senderUuid, receiverUuid)).isPresent();
    }
}

