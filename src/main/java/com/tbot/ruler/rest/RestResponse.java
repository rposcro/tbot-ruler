package com.tbot.ruler.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class RestResponse<T> {

    private ResponseEntity<T> entity;

    public T getBody() {
        return entity.getBody();
    }

    public int getStatusCode() {
        return entity.getStatusCodeValue();
    }
}
