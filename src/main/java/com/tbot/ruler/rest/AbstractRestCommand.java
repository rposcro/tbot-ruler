package com.tbot.ruler.rest;

import com.tbot.ruler.exceptions.RestRequestException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractRestCommand {

    private int connectionTimeout;
    private int readTimeout;
    private int retryCount;

    protected RestTemplate newRestTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(connectionTimeout)
            .setReadTimeout(readTimeout)
            .requestFactory(HttpComponentsClientHttpRequestFactory.class)
            .build();
    }

    protected RestResponse executeRequest(Supplier<RestResponse> requestExecutor) {
        int retries = 0;
        while (retries++ < retryCount) {
            try {
                return requestExecutor.get();
            } catch(RestClientException e) {
                log.info(String.format("Rest request failed, attempt %s of %s! %s", retries, retryCount, e.getMessage()));
            }
        }

        throw new RestRequestException("Failed to execute rest request!");
    }
}
