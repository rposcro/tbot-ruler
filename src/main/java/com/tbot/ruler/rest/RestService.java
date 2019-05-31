package com.tbot.ruler.rest;

import org.springframework.stereotype.Service;

import com.tbot.ruler.rest.RestGetCommand.RestGetCommandBuilder;
import com.tbot.ruler.rest.RestPatchCommand.RestPatchCommandBuilder;

@Service
public class RestService {

    static final int CONNECT_TIMEOUT = 1000;
    static final int READ_TIMEOUT = 2000;

    public RestGetCommandBuilder builderForGet() {
        return RestGetCommand.builder();
    }

    public RestPatchCommandBuilder builderForPatch() {
        return RestPatchCommand.builder();
    }
}
