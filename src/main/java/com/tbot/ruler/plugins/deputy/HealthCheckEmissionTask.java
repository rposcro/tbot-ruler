package com.tbot.ruler.plugins.deputy;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.ReportLog;
import com.tbot.ruler.broker.payload.ReportLogLevel;
import com.tbot.ruler.rest.RestGetCommand;
import com.tbot.ruler.rest.RestResponse;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
class  HealthCheckEmissionTask implements Runnable {

    private String actuatorId;
    private MessagePublisher messagePublisher;
    private RestGetCommand healthCheckCommand;

    private Optional<Boolean> isHealthy;

    @Builder
    public HealthCheckEmissionTask(
        @NonNull String actuatorId,
        @NonNull MessagePublisher messagePublisher,
        @NonNull RestGetCommand healthCheckCommand
    ) {
        this.actuatorId = actuatorId;
        this.messagePublisher = messagePublisher;
        this.healthCheckCommand = healthCheckCommand;
        this.isHealthy = Optional.empty();
    }

    @Override
    public void run() {
        try {
            log.info("[EMISSION] Deputy health check for actuator {}", actuatorId);
            RestResponse response = healthCheckCommand.sendGet();
            if (response.getStatusCode() == 200) {
                handleHealthy();
            } else {
                handleUnhealthy(response.getStatusCode());
            }
        }
        catch(Exception e) {
            log.info("Deputy health check failed: " + e.getMessage());
            handleUnhealthy(null);
        }
    }

    private void handleUnhealthy(Integer statusCode) {
        if (!isHealthy.isPresent() || isHealthy.get()) {
            isHealthy = Optional.of(false);
            messagePublisher.publishMessage(buildMessage(
                "Deputy node unhealthy: " + (statusCode == null ? "Unreachable" : statusCode.toString()),
                ReportLogLevel.IMPORTANT));
        }
    }

    private void handleHealthy() {
        if (!isHealthy.isPresent() || !isHealthy.get()) {
            isHealthy = Optional.of(true);
            messagePublisher.publishMessage(buildMessage("Deputy node is healthy", ReportLogLevel.REGULAR));
        }
    }

    private Message buildMessage(String message, ReportLogLevel entryLevel) {
        return Message.builder()
            .senderId(actuatorId)
            .payload(ReportLog.builder()
                    .logLevel(entryLevel)
                    .timestamp(ZonedDateTime.now())
                    .content(message)
                    .build())
            .build();
    }
}