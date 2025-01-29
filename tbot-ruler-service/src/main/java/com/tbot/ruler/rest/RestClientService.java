package com.tbot.ruler.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tbot.ruler.rest.RestClientGetCommand.RestClientGetCommandBuilder;
import com.tbot.ruler.rest.RestClientPatchCommand.RestClientPatchCommandBuilder;

@Service
public class RestClientService {

    @Value("${ruler.restService.connectionTimeout:5000}")
    private int connectionTimeout;
    @Value("${ruler.restService.readTimeout:25000}")
    private int readTimeout;
    @Value("${ruler.restService.retryCount:5}")
    private int retryCount;

    public RestClientGetCommandBuilder builderForGet() {
        return RestClientGetCommand.builder()
            .connectionTimeout(connectionTimeout)
            .readTimeout(readTimeout)
            .retryCount(retryCount);
    }

    public RestClientPatchCommandBuilder builderForPatch() {
        return RestClientPatchCommand.builder()
            .connectionTimeout(connectionTimeout)
            .readTimeout(readTimeout)
            .retryCount(retryCount);
    }
}
