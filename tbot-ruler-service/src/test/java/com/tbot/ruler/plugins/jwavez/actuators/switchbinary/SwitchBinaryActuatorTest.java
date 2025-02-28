package com.tbot.ruler.plugins.jwavez.actuators.switchbinary;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryClaim;
import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SwitchBinaryActuatorTest {

    @Mock
    private CommandSender commandSender;

    @Test
    public void testOnOffWhenStateIsUnknown() {
        SwitchBinaryActuator actuator = constructActuator();
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(BinaryState.ON)
                .build());

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertTrue(actuator.getState().getPayload().isOn());
    }

    @Test
    public void testOnOffWhenStateIsOn() {
        SwitchBinaryActuator actuator = constructActuator();
        actuator.setState(BinaryState.ON);
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(BinaryState.OFF)
                .build());

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertFalse(actuator.getState().getPayload().isOn());
    }

    @ParameterizedTest
    @EnumSource(BinaryClaim.class)
    public void testBinaryClaimWhenStateIsUnknown(BinaryClaim stateClaim) {
        SwitchBinaryActuator actuator = constructActuator();
        actuator.acceptMessage(Message.builder()
            .senderId("sender-id")
            .payload(stateClaim)
            .build());

        boolean expectedState = stateClaim.isToggle() ? true : stateClaim.isSetOn();

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertEquals(expectedState, actuator.getState().getPayload().isOn());
    }

    @ParameterizedTest
    @EnumSource(BinaryClaim.class)
    public void testBinaryClaimWhenStateIsOn(BinaryClaim stateClaim) {
        SwitchBinaryActuator actuator = constructActuator();
        actuator.setState(BinaryState.ON);
        actuator.acceptMessage(Message.builder()
            .senderId("sender-id")
            .payload(stateClaim)
            .build());

        boolean expectedState = stateClaim.isToggle() ? false : stateClaim.isSetOn();

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertEquals(expectedState, actuator.getState().getPayload().isOn());
    }

    private SwitchBinaryActuator constructActuator() {
        SwitchBinaryConfiguration configuration = new SwitchBinaryConfiguration();
        configuration.setNodeId(1);
        configuration.setMultiChannelOn(false);
        return SwitchBinaryActuator.builder()
            .uuid("actuator-uuid")
            .name("actuator-name")
            .description("actuator-description")
            .configuration(configuration)
            .commandSender(commandSender)
            .applicationSupport(JwzApplicationSupport.defaultSupport())
            .build();
    }
}
