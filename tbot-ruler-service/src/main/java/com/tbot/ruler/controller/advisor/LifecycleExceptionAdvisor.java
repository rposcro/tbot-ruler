package com.tbot.ruler.controller.advisor;

import com.tbot.ruler.broker.payload.ApplicationProblemDetails;
import com.tbot.ruler.exceptions.LifecycleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class LifecycleExceptionAdvisor {

    @ExceptionHandler(LifecycleException.class)
    public ResponseEntity<ApplicationProblemDetails> handleLifecycleException(LifecycleException ex) {
        log.warn("Lifecycle request execution failed!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Lifecycle request failed to complete!")
                .stackTrace(ex.toString())
                .build()
        );
    }
}
