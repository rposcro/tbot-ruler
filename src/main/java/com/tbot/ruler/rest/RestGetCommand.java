package com.tbot.ruler.rest;

import java.util.Collections;
import java.util.Map;

import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestGetCommand extends AbstractRestCommand {
    
    private String host;
    private String port;
    private String path;

    private Map<String, String> reqParams = Collections.emptyMap();

    @Builder
    public RestGetCommand(
        @NonNull String host,
        @NonNull String port,
        @NonNull String path,
        int connectionTimeout,
        int readTimeout,
        int retryCount
    ) {
        super(connectionTimeout, readTimeout, retryCount);
        this.host = host;
        this.port = port;
        this.path = path;
    }

    public RestResponse<String> sendGet() {
        return sendGet(String.class);
    }

    public <T> RestResponse<T> sendGet(Class<T> responseType) {
        String uri = uri();
        log.debug("Requested get for: " + uri);
        return executeRequest(() -> {
            RestTemplate restTmpl = newRestTemplate();
            ResponseEntity<T> entity = restTmpl.getForEntity(uri, responseType);
            return new RestResponse(entity);
        });
    }

    private String uri() {
        return UriComponentsBuilder
                .fromHttpUrl(host)
                .port(port)
                .path(path)
                .buildAndExpand(reqParams)
                .toUriString();
    }
}
