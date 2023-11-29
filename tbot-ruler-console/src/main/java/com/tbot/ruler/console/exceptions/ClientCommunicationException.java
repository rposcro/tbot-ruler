package com.tbot.ruler.console.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.controller.advisor.payload.ErrorResponse;
import lombok.Getter;
import retrofit2.Response;

import java.io.IOException;

import static java.lang.String.format;

@Getter
public class ClientCommunicationException extends RuntimeException {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private Response<?> response;

    public ClientCommunicationException(Response<?> response) {
        super(format("API error %s!", response.code()));
        this.response = response;
    }

    public ClientCommunicationException(Response<?> response, String resourceName) {
        super(format("API error %s when communicating to %s!", response.code(), resourceName));
        this.response = response;
    }

    public ClientCommunicationException(String message, Throwable t) {
        super(message, t);
    }

    public int getResponseCode() {
        return response == null ? 0 : response.code();
    }

    public String getResponseMessage() {
        if (response != null) {
            String errorBody;

            try {
                errorBody = response.errorBody().string();
            } catch(IOException e) {
                return response.message();
            }

            try {
                ErrorResponse responsePayload = MAPPER.readValue(errorBody, ErrorResponse.class);
                return responsePayload.getMessage();
            } catch(JsonProcessingException e) {
                return response.message();
            }
        }

        return null;
    }
}
