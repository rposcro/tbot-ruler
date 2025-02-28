package com.tbot.ruler.plugins.jwavez.actuators.switchmultilevel;

import com.rposcro.jwavez.core.JwzApplicationSupport;
import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryStateClaim;
import com.tbot.ruler.broker.payload.OnOffState;
import com.tbot.ruler.plugins.jwavez.controller.CommandSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SwitchMultilevelActuatorTest {

    @Mock
    private CommandSender commandSender;

    @Test
    public void testOnOffWhenStateIsUnknown() {
        SwitchMultilevelActuator actuator = constructActuator();
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(OnOffState.STATE_ON)
                .build());

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertTrue(actuator.getState().getPayload().isOn());
    }

    @Test
    public void testOnOffWhenStateIsOn() {
        SwitchMultilevelActuator actuator = constructActuator();
        actuator.setState(OnOffState.STATE_ON);
        actuator.acceptMessage(Message.builder()
                .senderId("sender-id")
                .payload(OnOffState.STATE_OFF)
                .build());

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertFalse(actuator.getState().getPayload().isOn());
    }

    @ParameterizedTest
    @EnumSource(BinaryStateClaim.class)
    public void testBinaryClaimWhenStateIsUnknown(BinaryStateClaim stateClaim) {
        SwitchMultilevelActuator actuator = constructActuator();
        actuator.acceptMessage(Message.builder()
            .senderId("sender-id")
            .payload(stateClaim)
            .build());

        boolean expectedState = stateClaim.isToggle() ? true : stateClaim.isSetOn();

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertEquals(expectedState, actuator.getState().getPayload().isOn());
    }

    @ParameterizedTest
    @EnumSource(BinaryStateClaim.class)
    public void testBinaryClaimWhenStateIsOn(BinaryStateClaim stateClaim) {
        SwitchMultilevelActuator actuator = constructActuator();
        actuator.setState(OnOffState.STATE_ON);
        actuator.acceptMessage(Message.builder()
            .senderId("sender-id")
            .payload(stateClaim)
            .build());

        boolean expectedState = stateClaim.isToggle() ? false : stateClaim.isSetOn();

        verify(commandSender, times(1)).enqueueCommand(any(NodeId.class), any(ZWaveControlledCommand.class));
        assertEquals(expectedState, actuator.getState().getPayload().isOn());
    }

    private SwitchMultilevelActuator constructActuator() {
        SwitchMultilevelConfiguration configuration = new SwitchMultilevelConfiguration();
        configuration.setNodeId(1);
        return SwitchMultilevelActuator.builder()
            .uuid("actuator-uuid")
            .name("actuator-name")
            .description("actuator-description")
            .nodeId(new NodeId(1))
            .commandSender(commandSender)
            .applicationSupport(JwzApplicationSupport.defaultSupport())
            .build();
    }
}
