package com.tbot.ruler.rest;

import com.tbot.ruler.exceptions.RestClientRequestException;
import java.time.Duration;
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
public class AbstractRestClientCommand {

    private int connectionTimeout;
    private int readTimeout;
    private int retryCount;

    protected RestTemplate newRestTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(connectionTimeout))
            .setReadTimeout(Duration.ofMillis(readTimeout))
            .requestFactory(HttpComponentsClientHttpRequestFactory.class)
            .build();
    }

    protected RestClientResponse executeRequest(Supplier<RestClientResponse> requestExecutor) {
        int retries = 0;
        while (retries++ < retryCount) {
            try {
                return requestExecutor.get();
            } catch(RestClientException e) {
                log.info(String.format("Rest request failed, attempt %s of %s! %s", retries, retryCount, e.getMessage()));
            }
        }

        throw new RestClientRequestException("Failed to execute rest request!");
    }
}
