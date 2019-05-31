package com.tbot.ruler.rest;

import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@AllArgsConstructor
public class RestPatchCommand {
    
    private String host;
    private String port;
    private String path;
    
    public RestResponse sendPatch(Map<String, String> reqParams) {
        String uri = uri(toHttpHeaders(reqParams));
        log.debug("Requested patch for: " + uri);
        RestTemplate restTmpl = new RestTemplateBuilder()
                .setConnectTimeout(RestService.CONNECT_TIMEOUT)
                .setReadTimeout(RestService.READ_TIMEOUT)
                .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                .build();

        ResponseEntity<String> response = restTmpl.exchange(uri, HttpMethod.PATCH, null, String.class);
        return new RestResponse(response);
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
