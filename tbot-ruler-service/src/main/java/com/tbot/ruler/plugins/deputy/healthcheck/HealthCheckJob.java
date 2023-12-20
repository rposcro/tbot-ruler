package com.tbot.ruler.plugins.deputy.healthcheck;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.MessagePublisher;
import com.tbot.ruler.broker.payload.ReportLog;
import com.tbot.ruler.broker.payload.ReportLogLevel;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.plugins.deputy.api.DeputyServiceApi;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
class HealthCheckJob implements Job {

    private String actuatorUuid;
    private MessagePublisher messagePublisher;
    private DeputyServiceApi deputyServiceApi;

    private Optional<Boolean> isHealthy;

    @Builder
    public HealthCheckJob(
        @NonNull String actuatorUuid,
        @NonNull MessagePublisher messagePublisher,
        @NonNull DeputyServiceApi deputyServiceApi
    ) {
        this.actuatorUuid = actuatorUuid;
        this.messagePublisher = messagePublisher;
        this.deputyServiceApi = deputyServiceApi;
        this.isHealthy = Optional.empty();
    }

    @Override
    public void doJob() {
        try {
            log.info("[EMISSION] Deputy health check for actuator {}", actuatorUuid);
            Response<?> response = deputyServiceApi.ping().execute();
            if (response.isSuccessful()) {
                handleHealthy();
            } else {
                handleUnhealthy(response.code());
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
            .senderId(actuatorUuid)
            .payload(ReportLog.builder()
                    .logLevel(entryLevel)
                    .timestamp(ZonedDateTime.now())
                    .content(message)
                    .build())
            .build();
    }
}