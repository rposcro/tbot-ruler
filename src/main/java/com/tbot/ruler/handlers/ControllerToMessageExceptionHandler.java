package com.tbot.ruler.handlers;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.exceptions.MessageUnsupportedException;
import com.tbot.ruler.model.ApplicationProblemDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ControllerToMessageExceptionHandler {

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ApplicationProblemDetails> handleMessageException(HttpServletRequest request, MessageException ex) {
        log.warn("Message exception!", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Internal message processing failure!")
                .stackTrace(ex.toString())
                .build()
        );
    }

    @ExceptionHandler(MessageProcessingException.class)
    public ResponseEntity<ApplicationProblemDetails> handleMessageProcessingException(HttpServletRequest request, MessageProcessingException ex) {
        log.warn("Message could not be processed!", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Message processing failure!")
                .stackTrace(ex.toString())
                .build()
        );
    }

    @ExceptionHandler(MessageUnsupportedException.class)
    public ResponseEntity<ApplicationProblemDetails> handleMessageUnsupportedException(HttpServletRequest request, MessageUnsupportedException ex) {
        log.warn("Message is not supported by receiver!", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "application/json")
            .body(ApplicationProblemDetails.builder()
                .message("Bad message!")
                .stackTrace(ex.toString())
                .build()
        );
    }
}
