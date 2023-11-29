package com.tbot.ruler.console.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.controller.advisor.payload.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
public abstract class AbstractApiClient {

    @Autowired
    private ObjectMapper objectMapper;

    protected <T> T executeApiFunction(ApiFunction<Response<T>> function) {
        try {
            Response<T> response = function.runProcedure();
            if (!response.isSuccessful()) {
                log.error("Error response {} from TBot Ruler Service: {}", response.code(), response.message());
                throw new ClientCommunicationException(response, "TBot Ruler Service");
            }
            return response.body();
        } catch(IOException e) {
            log.error("Exception from TBot Ruler Service", e);
            throw new ClientCommunicationException("Exception from TBot Ruler Service: " + e.getMessage(), e);
        }
    }
}
