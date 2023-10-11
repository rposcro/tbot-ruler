package com.tbot.ruler.controller.advisor;

import com.tbot.ruler.broker.payload.ApplicationProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationProblemDetails> handleException(Exception ex) {
        log.error("Unhandled internal exception!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Unhandled internal exception!")
                .stackTrace(ex.toString())
                .build()
        );
    }
}
