package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.core.commands.controlled.ZWaveControlledCommand;
import com.rposcro.jwavez.core.model.NodeId;
import com.rposcro.jwavez.serial.JwzSerialSupport;
import com.rposcro.jwavez.serial.SerialRequestFactory;
import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;
import com.tbot.ruler.jobs.Job;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CommandSender implements Job {

    private final static long MAX_ACTIVATION_WAIT_TIME = 300_000;

    private SerialController serialController;
    private SerialRequestFactory serialRequestFactory;
    private long sleepTimeOnNotActiveController;

    private final LinkedBlockingDeque<SerialRequest> requestQueue;
    private final AtomicInteger callbackId;

    @Builder
    public CommandSender(SerialController serialController) {
        this.serialController = serialController;
        this.serialRequestFactory = JwzSerialSupport.defaultSupport().serialRequestFactory();
        this.sleepTimeOnNotActiveController = 1000;
        this.requestQueue = new LinkedBlockingDeque<>(100);
        this.callbackId = new AtomicInteger(1);
    }

    public void enqueueCommand(NodeId nodeId, ZWaveControlledCommand command) {
        enqueueCommand(nodeId, command, false);
    }

    public void enqueuePrioritizedCommand(NodeId nodeId, ZWaveControlledCommand command) {
        enqueueCommand(nodeId, command, true);
    }

    @Override
    public void doJob() {
        log.info("JWaveZ Command Sender thread is running");
        waitForJwzController();

        try {
            SerialRequest request = requestQueue.take();
            log.debug("Requested Z-Wave frame to send ...");
            serialController.sendRequest(request);
        } catch(SerialException e) {
            log.error("Exception when sending Z-Wave command!", e);
        } catch(InterruptedException e) {
            log.error("Interrupted Z-Wave sender thread sleep!", e);
        }
    }

    private void enqueueCommand(NodeId nodeId, ZWaveControlledCommand command, boolean prioritized) {
        SerialRequest request = serialRequestFactory.networkTransportRequestBuilder()
            .createSendDataRequest(nodeId, command, nextCallbackId());

        if (requestQueue.remainingCapacity() < 1
            || (!prioritized && !requestQueue.offer(request))
            || (prioritized && !requestQueue.offerFirst(request))) {
            log.warn("Request queue is full, dropped command {} for node {}", command.getClass(), nodeId.getId());
        } else {
            log.debug("Enqueued {} command request {} to node {}",
                prioritized ? "prioritized" : "regular", command.getClass(), nodeId.getId());
        }
    }

    private void waitForJwzController() {
        while(!serialController.isConnected()) {
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

    @Override
    public String getJobName() {
        return "JwzCommandSender";
    }
}
