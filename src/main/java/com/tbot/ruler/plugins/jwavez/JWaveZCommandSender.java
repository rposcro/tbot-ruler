package com.tbot.ruler.plugins.jwavez;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.controllers.GeneralAsynchronousController;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.frames.requests.SendDataRequest;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class JWaveZCommandSender implements Runnable {

    private final static long MAX_ACTIVATION_WAIT_TIME = 300_000;

    @Setter
    private GeneralAsynchronousController jwzController;
    private long sleepTimeOnNotActiveController;

    private final LinkedBlockingQueue<SerialRequest> requestQueue;
    private final AtomicInteger callbackId;

    public JWaveZCommandSender() {
        this.jwzController = null;
        this.sleepTimeOnNotActiveController = 1000;
        this.requestQueue = new LinkedBlockingQueue<>(100);
        this.callbackId = new AtomicInteger(1);
    }

    public void enqueueCommand(NodeId nodeId, ZWaveControlledCommand command) {
        SerialRequest request = SendDataRequest.createSendDataRequest(nodeId, command, nextCallbackId());

        if (requestQueue.remainingCapacity() < 1 || !requestQueue.offer(request)) {
            log.warn("Request queue is full, dropped command {} for node {}", command.getClass(), nodeId.getId());
        } else {
            log.debug("Enqueued command request {} from node {}",command.getClass(), nodeId.getId());
        }
    }

    public void run() {
        log.info("JWaveZ Command Sender thread is running");
        waitForJwzController();

        while(true) {
            try {
                SerialRequest request = requestQueue.take();
                log.debug("Requested Z-Wave frame to send ...");
                jwzController.requestCallbackFlow(request);
            } catch(SerialException e) {
                log.error("Exception when sending Z-Wave command!", e);
            } catch(InterruptedException e) {
                log.error("Interrupted Z-Wave sender thread sleep!", e);
            }
        }
    }

    private void waitForJwzController() {
        while(jwzController == null) {
            log.warn("JwzController is not active, waiting for {} to activate", sleepTimeOnNotActiveController);
            try {
                Thread.sleep(sleepTimeOnNotActiveController);
                sleepTimeOnNotActiveController = Math.min(MAX_ACTIVATION_WAIT_TIME, sleepTimeOnNotActiveController * 2);
            } catch(InterruptedException e) {
                log.error("Interrupted awaiting period for JwzController activation!", e);
            }
        }

        log.info("Jwz Controller is active");
    }

    private byte nextCallbackId() {
        return (byte) callbackId.accumulateAndGet(1, (current, add) -> (++current > 250 ? 1 : current));
    }
}
