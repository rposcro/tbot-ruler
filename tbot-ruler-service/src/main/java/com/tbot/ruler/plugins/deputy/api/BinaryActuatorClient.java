package com.tbot.ruler.plugins.deputy.api;

import com.tbot.ruler.exceptions.MessageProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
@Builder
@AllArgsConstructor
public class BinaryActuatorClient {

    private static String VALUE_ON = "on";
    private static String VALUE_OFF = "off";

    private int pinNumber;
    @NonNull private DeputyServiceApi deputyServiceApi;

    public void updateState(boolean state) {
        try {
            Response<?> response = deputyServiceApi.setBinaryState(pinNumber, state ? VALUE_ON : VALUE_OFF).execute();

            if (!response.isSuccessful()) {
                log.info("Bin-out state change request failed! " + response.code());
                throw new MessageProcessingException("Failed to request bin-out state change! Status code returned: " + response.code());
            }
        }
        catch(IOException e) {
            log.info("Bin-out state change request failed! " + e.getMessage());
            throw new MessageProcessingException(e);
        }
    }

    public boolean requestState() {
        try {
            Response<BinOutStateResponse> response = deputyServiceApi.getBinaryState(pinNumber).execute();

            if (!response.isSuccessful()) {
                log.info("Bin-out state read request failed! " + response.code());
                throw new MessageProcessingException("Failed to request bin-out state value! Status code returned: " + response.code());
            }

            return VALUE_ON.equalsIgnoreCase(response.body().getState());
        }
        catch(IOException e) {
            log.info("Bin-out state read request failed! " + e.getMessage());
            throw new MessageProcessingException(e);
        }
    }
}
