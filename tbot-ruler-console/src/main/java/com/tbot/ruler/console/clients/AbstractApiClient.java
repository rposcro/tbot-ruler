package com.tbot.ruler.console.clients;

import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
public class AbstractApiClient {

    protected <T> T executeApiFunction(ApiFunction<Response<T>> function) {
        try {
            Response<T> response = function.runProcedure();
            if (!response.isSuccessful()) {
                throw new ClientCommunicationException(response.code(), "Error %s from TBot Ruler Service!", response.code());
            }
            return response.body();
        } catch(IOException e) {
            log.error("Exception from TBot Ruler Service", e);
            throw new ClientCommunicationException("Exception from TBot Ruler Service: " + e.getMessage(), e);
        }
    }
}
