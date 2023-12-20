package com.tbot.ruler.controller.advisor;

import com.tbot.ruler.controller.advisor.payload.ErrorResponse;
import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.exceptions.ServiceException;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.exceptions.ServiceTimeoutException;
import com.tbot.ruler.exceptions.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LifecycleException.class)
    public ResponseEntity<ErrorResponse> handleLifecycleException(LifecycleException ex) {
        log.warn("Lifecycle request execution failed!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
        log.warn("Message exception!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Internal message processing failure!")
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(MessageProcessingException.class)
    public ResponseEntity<ErrorResponse> handleMessageProcessingException(MessageProcessingException ex) {
        log.warn("Message could not be processed!", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Message processing failure!")
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(MessageUnsupportedException.class)
    public ResponseEntity<ErrorResponse> handleMessageUnsupportedException(MessageUnsupportedException ex) {
        log.warn("Message is not supported by receiver!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Bad message!")
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        log.warn("Service execution failed!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Service failed to complete!")
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.warn("Service unable to complete!", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Service could not complete due to items unavailable!")
                        .payload(ex.getUnavailableItems())
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(ServiceTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleServiceTimeoutException(ServiceTimeoutException ex) {
        log.warn("Service timed out!", ex);
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Service could not complete within scheduled time!")
                        .stackTrace(ex.toString())
                        .build()
                );
    }

    @ExceptionHandler(ServiceRequestException.class)
    public ResponseEntity<ErrorResponse> handleServiceRequestException(ServiceRequestException ex) {
        log.warn("Bad service request!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Bad service request! " + ex.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled internal exception!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(ErrorResponse.builder()
                        .message("Unhandled internal exception!")
                        .stackTrace(ex.toString())
                        .build()
                );
    }
}
