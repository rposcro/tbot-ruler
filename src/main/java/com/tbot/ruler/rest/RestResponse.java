package com.tbot.ruler.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class RestResponse {

    private ResponseEntity<String> entity;

    public String getBody() {
        return entity.getBody();
    }

    public int getStatusCode() {
        return entity.getStatusCodeValue();
    }
}
