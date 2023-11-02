package com.tbot.ruler.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tbot.ruler.rest.RestGetCommand.RestGetCommandBuilder;
import com.tbot.ruler.rest.RestPatchCommand.RestPatchCommandBuilder;

@Service
public class RestService {

    @Value("${ruler.restService.connectionTimeout:5000}")
    private int connectionTimeout;
    @Value("${ruler.restService.readTimeout:25000}")
    private int readTimeout;
    @Value("${ruler.restService.retryCount:5}")
    private int retryCount;

    public RestGetCommandBuilder builderForGet() {
        return RestGetCommand.builder()
            .connectionTimeout(connectionTimeout)
            .readTimeout(readTimeout)
            .retryCount(retryCount);
    }

    public RestPatchCommandBuilder builderForPatch() {
        return RestPatchCommand.builder()
            .connectionTimeout(connectionTimeout)
            .readTimeout(readTimeout)
            .retryCount(retryCount);
    }
}
