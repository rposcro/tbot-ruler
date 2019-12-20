package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.message.Message;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestResponse;
import com.tbot.ruler.things.ItemId;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

import static com.tbot.ruler.message.payloads.BooleanUpdatePayload.UPDATE_FALSE;
import static com.tbot.ruler.message.payloads.BooleanUpdatePayload.UPDATE_TRUE;

@Slf4j
class HealthCheckEmissionTask implements Runnable {

    private ItemId emitterId;
    private Consumer<Message> messageConsumer;
    private RestGetCommand healthCheckCommand;

    private Message messageHealthy;
    private Message messageSick;

    @Builder
    public HealthCheckEmissionTask(
        @NonNull ItemId emitterId,
        @NonNull Consumer<Message> messageConsumer,
        @NonNull RestGetCommand healthCheckCommand
    ) {
        this.emitterId = emitterId;
        this.messageConsumer = messageConsumer;
        this.healthCheckCommand = healthCheckCommand;
        this.messageHealthy = Message.builder().senderId(emitterId).payload(UPDATE_TRUE).build();
        this.messageSick = Message.builder().senderId(emitterId).payload(UPDATE_FALSE).build();
    }

    @Override
    public void run() {
        try {
            log.info("[EMISSION] Deputy health check for emitter {}", emitterId.getValue());
            RestResponse response = healthCheckCommand.sendGet();
            if (response.getStatusCode() == 200) {
                messageConsumer.accept(messageHealthy);
            }
        }
        catch(Exception e) {
            log.info("Deputy health check failed: " + e.getMessage());
            messageConsumer.accept(messageSick);
        }
    }
}