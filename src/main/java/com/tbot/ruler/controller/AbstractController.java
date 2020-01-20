package com.tbot.ruler.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class AbstractController implements ControllerConstants {

    protected URI controllerURI(Class<?> controllerClass, String endpointMethodName, Object... parameters) {
        List<Method> methods = Stream.of(controllerClass.getMethods())
            .filter(method -> method.getName().equals(endpointMethodName))
            .collect(Collectors.toList());

        if (methods.size() < 1) {
            throw new IllegalArgumentException("Expected unique method name, no method named " + endpointMethodName + " found!");
        } else if (methods.size() != 1) {
            throw new IllegalArgumentException("Expected unique method name, found " + methods.size() + " methods named " + endpointMethodName + "!");
        }

        return ControllerLinkBuilder.linkTo(controllerClass, methods.get(0), parameters).toUri();
    }

    protected ResponseEntity.BodyBuilder response(ResponseEntity.BodyBuilder builder) {
        return builder.header("Customer-Header", "Provided by TBot");
    }

    protected ResponseEntity.HeadersBuilder response(ResponseEntity.HeadersBuilder builder) {
        return builder.header("Customer-Header", "Provided by TBot");
    }
}
