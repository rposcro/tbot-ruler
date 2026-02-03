package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandSenderTest {

    @Mock
    private SerialController serialController;

    private CommandSender commandSender;

    @BeforeEach
    public void setUp() {
        this.commandSender = CommandSender.builder()
            .serialController(serialController)
            .build();
        when(serialController.isConnected()).thenReturn(true);
    }

    @Test
    public void doJobSendsRegularCommand() throws Exception {
        NodeId nodeId = new NodeId(90);
        commandSender.enqueueCommand(nodeId, new ZWaveControlledCommand());

        assertSending(nodeId);
    }

    @Test
    public void doJobSendsPrioritizedCommand() throws Exception {
        NodeId nodeId = new NodeId(91);
        commandSender.enqueuePrioritizedCommand(nodeId, new ZWaveControlledCommand());

        assertSending(nodeId);
    }

    @Test
    public void doJobSendsCommandsInRightOrder() throws Exception {
        NodeId regularNodeId1 = new NodeId(90);
        NodeId regularNodeId2 = new NodeId(91);
        NodeId prioritizedNodeId1 = new NodeId(95);
        NodeId prioritizedNodeId2 = new NodeId(96);

        commandSender.enqueueCommand(regularNodeId1, new ZWaveControlledCommand());
        commandSender.enqueuePrioritizedCommand(prioritizedNodeId1, new ZWaveControlledCommand());
        commandSender.enqueueCommand(regularNodeId2, new ZWaveControlledCommand());
        commandSender.enqueuePrioritizedCommand(prioritizedNodeId2, new ZWaveControlledCommand());

        assertSending(prioritizedNodeId2);
        assertSending(prioritizedNodeId1);
        assertSending(regularNodeId1);
        assertSending(regularNodeId2);
    }

    private void assertSending(NodeId expectedNodeId) throws Exception{
        commandSender.doJob();
        ArgumentCaptor<SerialRequest> serialRequestCaptor = ArgumentCaptor.forClass(SerialRequest.class);
        verify(serialController, atMost(4)).sendRequest(serialRequestCaptor.capture());
        SerialRequest serialCommand = serialRequestCaptor.getValue();

        assertEquals(expectedNodeId.getId(), serialCommand.getFrameData().getByte(4));
    }
}
