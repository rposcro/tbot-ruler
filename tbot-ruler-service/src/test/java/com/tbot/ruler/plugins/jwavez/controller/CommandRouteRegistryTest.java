package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.supported.ZWaveSupportedCommand;
import com.rposcro.jwavez.core.commands.types.CommandType;
import com.tbot.ruler.exceptions.PluginException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.rposcro.jwavez.core.commands.types.BasicCommandType.BASIC_SET;
import static com.rposcro.jwavez.core.commands.types.NotificationCommandType.NOTIFICATION_REPORT;
import static com.rposcro.jwavez.core.commands.types.NotificationCommandType.NOTIFICATION_SUPPORTED_GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandRouteRegistryTest {

    @Test
    public void findsRegisteredListeners() {
        CommandListener<ZWaveSupportedCommand> l1 = mockListener(BASIC_SET);
        CommandListener<ZWaveSupportedCommand> l2 = mockListener(NOTIFICATION_REPORT);
        CommandListener<ZWaveSupportedCommand> l3 = mockListener(BASIC_SET);

        CommandRouteRegistry registry = new CommandRouteRegistry();
        registry.registerListener(l1);
        registry.registerListener(l2);
        registry.registerListener(l3);

        List<CommandListener<ZWaveSupportedCommand>> listeners = registry.findListeners(BASIC_SET);
        assertEquals(2, listeners.size());
        assertTrue(listeners.contains(l1));
        assertTrue(listeners.contains(l3));

        listeners = registry.findListeners(NOTIFICATION_REPORT);
        assertEquals(1, listeners.size());
        assertTrue(listeners.contains(l2));

        listeners = registry.findListeners(NOTIFICATION_SUPPORTED_GET);
        assertEquals(0, listeners.size());
    }

    @Test
    public void doesntFindUnregisteredListeners() {
        CommandListener<ZWaveSupportedCommand> l1 = mockListener(BASIC_SET);
        CommandListener<ZWaveSupportedCommand> l2 = mockListener(NOTIFICATION_REPORT);
        CommandListener<ZWaveSupportedCommand> l3 = mockListener(NOTIFICATION_REPORT);
        CommandListener<ZWaveSupportedCommand> l4 = mockListener(BASIC_SET);

        CommandRouteRegistry registry = new CommandRouteRegistry();
        registry.registerListener(l1);
        registry.registerListener(l2);
        registry.registerListener(l3);
        registry.registerListener(l4);

        assertEquals(2, registry.findListeners(BASIC_SET).size());
        assertEquals(2, registry.findListeners(NOTIFICATION_REPORT).size());

        registry.unregisterListener(l1.getListenerKey());
        assertEquals(1, registry.findListeners(BASIC_SET).size());
        assertEquals(2, registry.findListeners(NOTIFICATION_REPORT).size());
        assertTrue(registry.findListeners(BASIC_SET).contains(l4));
        assertTrue(registry.findListeners(NOTIFICATION_REPORT).contains(l2));
        assertTrue(registry.findListeners(NOTIFICATION_REPORT).contains(l3));

        registry.unregisterListener(l2.getListenerKey());
        registry.unregisterListener(l3.getListenerKey());
        assertEquals(1, registry.findListeners(BASIC_SET).size());
        assertEquals(0, registry.findListeners(NOTIFICATION_REPORT).size());
    }

    @Test
    public void multipleUnregistersIsAccepted() {
        CommandListener<ZWaveSupportedCommand> l1 = mockListener(BASIC_SET);
        CommandRouteRegistry registry = new CommandRouteRegistry();

        registry.registerListener(l1);
        registry.unregisterListener(l1.getListenerKey());
        registry.unregisterListener(l1.getListenerKey());

        assertEquals(0, registry.findListeners(BASIC_SET).size());
    }

    @Test
    public void throwsWhenListenerAlreadyRegistered() {
        CommandListener<ZWaveSupportedCommand> l1 = mockListener(BASIC_SET);
        CommandRouteRegistry registry = new CommandRouteRegistry();
        registry.registerListener(l1);

        assertThrows(PluginException.class, () -> registry.registerListener(l1));
    }

    private CommandListener<ZWaveSupportedCommand> mockListener(CommandType commandType) {
        CommandListener<ZWaveSupportedCommand> mock = mock(CommandListener.class);
        when(mock.getListenerKey()).thenReturn(UUID.randomUUID().toString());
        when(mock.getCommandType()).thenReturn(commandType);
        return mock;
    }
}
