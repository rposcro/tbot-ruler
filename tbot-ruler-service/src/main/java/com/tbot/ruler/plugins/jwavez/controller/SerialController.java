package com.tbot.ruler.plugins.jwavez.controller;

import com.rposcro.jwavez.serial.exceptions.SerialException;
import com.rposcro.jwavez.serial.rxtx.SerialRequest;

public interface SerialController {

    void connect();

    boolean isConnected();

    void sendRequest(SerialRequest serialRequest) throws SerialException;
}
