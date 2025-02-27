package com.tbot.ruler.plugins.jwavez.actuators.centralscene;

import com.rposcro.jwavez.core.buffer.ImmutableBuffer;
import com.rposcro.jwavez.core.commands.supported.centralscene.CentralSceneNotification;
import com.rposcro.jwavez.core.model.CentralSceneKeyAttribute;
import com.rposcro.jwavez.core.model.NodeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CentralSceneHoldCommandListenerTest {

    private final static int SOURCE_NODE_ID = 1;
    private final static int SOURCE_SCENE_ID = 2;

    @Mock
    private CentralSceneHoldActuator actuator;

    private CentralSceneHoldCommandListener listener;

    @BeforeEach
    public void setUp() {
        listener = CentralSceneHoldCommandListener.builder()
            .actuator(actuator)
            .sourceNodeId(SOURCE_NODE_ID)
            .sceneId(SOURCE_SCENE_ID)
            .build();
    }

    @ParameterizedTest
    @EnumSource(value = CentralSceneKeyAttribute.class, names = {"KEY_HELD_DOWN", "KEY_RELEASED"})
    public void testCommandIsAcceptedByFilter(CentralSceneKeyAttribute keyAttribute) {
        ImmutableBuffer commandBuffer = ImmutableBuffer.overBuffer(new byte[] {
            0x5b, 0x03, 0x1d, keyAttribute.getCode(), (byte) SOURCE_SCENE_ID });
        CentralSceneNotification notification = new CentralSceneNotification(commandBuffer, new NodeId(SOURCE_NODE_ID));

        assertTrue(listener.getCommandFilter().accepts(notification));
    }

    @ParameterizedTest
    @EnumSource(value = CentralSceneKeyAttribute.class, names = {"KEY_HELD_DOWN", "KEY_RELEASED"}, mode = EnumSource.Mode.EXCLUDE)
    public void testCommandIsDeclinedByFilter(CentralSceneKeyAttribute keyAttribute) {
        ImmutableBuffer commandBuffer = ImmutableBuffer.overBuffer(new byte[] {
            0x5b, 0x03, 0x1d, keyAttribute.getCode(), (byte) SOURCE_SCENE_ID });
        CentralSceneNotification notification = new CentralSceneNotification(commandBuffer, new NodeId(SOURCE_NODE_ID));

        assertFalse(listener.getCommandFilter().accepts(notification));
    }

    @Test
    public void testCommandIsForwardedToActuator() {
        ImmutableBuffer commandBuffer = ImmutableBuffer.overBuffer(new byte[] {
            0x5b, 0x03, 0x1d, CentralSceneKeyAttribute.KEY_HELD_DOWN.getCode(), (byte) SOURCE_SCENE_ID });
        CentralSceneNotification notification = new CentralSceneNotification(commandBuffer, new NodeId(SOURCE_NODE_ID));
        listener.handleCommand(notification);

        ArgumentCaptor<CentralSceneKeyAttribute> keyAttributeCaptor = ArgumentCaptor.forClass(CentralSceneKeyAttribute.class);
        verify(actuator).handleCommandKey(keyAttributeCaptor.capture());

        assertEquals(CentralSceneKeyAttribute.KEY_HELD_DOWN, keyAttributeCaptor.getValue());
    }
}
