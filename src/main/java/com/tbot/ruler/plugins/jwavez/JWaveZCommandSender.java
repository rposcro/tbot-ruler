package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.tbot.ruler.exceptions.MessageProcessingException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class JWaveZCommandSender implements Runnable {

    @Setter
    private GeneralAsynchronousController jwzController;

    private final LinkedBlockingQueue<SerialRequest> requestQueue;
    private final AtomicInteger callbackId;

    public JWaveZCommandSender() {
        this.jwzController = null;
        this.requestQueue = new LinkedBlockingQueue<>(100);
        this.callbackId = new AtomicInteger(1);
    }

    public void enqueueCommand(NodeId nodeId, ZWaveControlledCommand command) {
        if (jwzController == null) {
            throw new MessageProcessingException("JWaveZ controller is not active!");
        }

        SerialRequest request = SendDataRequest.createSendDataRequest(nodeId, command, nextCallbackId());

        if (requestQueue.remainingCapacity() < 1 || !requestQueue.offer(request)) {
            log.warn("Request queue is full, dropped command {} for node {}", command.getClass(), nodeId.getId());
        } else {
            log.debug("Enqueued command request {} from node {}",command.getClass(), nodeId.getId());
        }
    }

    public void run() {
        while(true) {
            try {
                SerialRequest request = requestQueue.take();
                log.debug("Requested frame to send ...");
                jwzController.requestCallbackFlow(request);
            } catch(SerialException e) {
                throw new MessageProcessingException("Exception when sending command!", e);
            } catch(InterruptedException e) {
                log.error("Interrupted request queue take operation!", e);
            }
        }
    }

    private byte nextCallbackId() {
        return (byte) callbackId.accumulateAndGet(1, (current, add) -> (++current > 250 ? 1 : current));
    }
}
