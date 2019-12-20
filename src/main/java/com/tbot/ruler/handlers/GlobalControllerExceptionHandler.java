package com.tbot.ruler.handlers;

import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.model.ApplicationProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApplicationProblemDetails> handleException(HttpServletRequest request, Exception ex) {
        log.error("Unhandled internal exception!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Unhandled internal exception!")
                .stackTrace(ex.toString())
                .build()
        );
    }

    @ExceptionHandler(MessageProcessingException.class)
    public ResponseEntity<ApplicationProblemDetails> handleMessageException(HttpServletRequest request, MessageProcessingException ex) {
        log.warn("Bad rest message detected!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Bad message!")
                .stackTrace(ex.toString())
                .build()
        );
    }
}
