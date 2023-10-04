package com.tbot.ruler.controller.advisor;

import com.tbot.ruler.exceptions.*;
import com.tbot.ruler.broker.payload.ApplicationProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerToServiceExceptionAdvisor {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApplicationProblemDetails> handleServiceException(ServiceException ex) {
        log.warn("Service execution failed!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Service failed to complete!")
                .stackTrace(ex.toString())
                .build()
        );
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApplicationProblemDetails> handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.warn("Service unable to complete!", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Service could not complete due to items unavailable!")
                .payload(ex.getUnavailableItems())
                .stackTrace(ex.toString())
                .build()
        );
    }

    @ExceptionHandler(ServiceTimeoutException.class)
    public ResponseEntity<ApplicationProblemDetails> handleServiceTimeoutException(ServiceTimeoutException ex) {
        log.warn("Service timed out!", ex);
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Service could not complete within scheduled time!")
                .stackTrace(ex.toString())
                .build()
        );
    }

    @ExceptionHandler(ServiceRequestException.class)
    public ResponseEntity<ApplicationProblemDetails> handleServiceRequestException(ServiceRequestException ex) {
        log.warn("Bad service request!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Bad service request! " + ex.getMessage())
                .build()
        );
    }
}
