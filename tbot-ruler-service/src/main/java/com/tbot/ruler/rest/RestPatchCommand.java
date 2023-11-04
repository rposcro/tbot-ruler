package com.tbot.ruler.rest;

import java.util.Map;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestPatchCommand extends AbstractRestCommand {
    
    private String host;
    private String port;
    private String path;

    @Builder
    public RestPatchCommand(
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

    public RestResponse sendPatch(Map<String, String> reqParams) {
        String uri = uri(toHttpHeaders(reqParams));
        log.debug("Requested patch for: " + uri);
        return executeRequest(() -> {
            RestTemplate restTmpl = newRestTemplate();
            ResponseEntity<String> response = restTmpl.exchange(uri, HttpMethod.PATCH, null, String.class);
            return new RestResponse(response);
        });
    }

    private HttpHeaders toHttpHeaders(Map<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        params.forEach((key, value) -> headers.add(key, value));
        return headers;
    }

    private String uri(MultiValueMap<String, String> reqParams) {
        return UriComponentsBuilder
                .fromHttpUrl(host)
                .port(port)
                .path(path)
                .queryParams(reqParams)
                .build()
                .toUriString();
    }
}
