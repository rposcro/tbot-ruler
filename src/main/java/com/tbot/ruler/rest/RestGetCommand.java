package com.tbot.ruler.rest;

import java.util.Collections;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class RestGetCommand {
    
    private String host;
    private String port;
    private String path;
    @Builder.Default
    private Map<String, String> reqParams = Collections.emptyMap();
    
    public RestResponse sendGet() {
        String uri = uri();
        log.debug("Requested get for: " + uri);
        RestTemplate restTmpl = new RestTemplateBuilder()
                .setConnectTimeout(RestService.CONNECT_TIMEOUT)
                .setReadTimeout(RestService.READ_TIMEOUT)
                .build();
        ResponseEntity<String> entity = restTmpl.getForEntity(uri, String.class);
        return new RestResponse(entity);
    }

    public <T> ResponseEntity<T> sendGet(Class<T> responseType) {
        String uri = uri();
        log.debug("Requested get for: " + uri);
        RestTemplate restTmpl = new RestTemplateBuilder()
            .setConnectTimeout(RestService.CONNECT_TIMEOUT)
            .setReadTimeout(RestService.READ_TIMEOUT)
            .build();
        ResponseEntity<T> entity = restTmpl.getForEntity(uri, responseType);
        return entity;
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
