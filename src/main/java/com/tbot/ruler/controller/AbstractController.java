package com.tbot.ruler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class AbstractController implements ControllerConstants {

    protected ResponseEntity.BodyBuilder response(ResponseEntity.BodyBuilder builder) {
        return builder.header("Customer-Header", "Provided by TBot");
    }

    protected ResponseEntity.HeadersBuilder response(ResponseEntity.HeadersBuilder builder) {
        return builder.header("Customer-Header", "Provided by TBot");
    }
}
