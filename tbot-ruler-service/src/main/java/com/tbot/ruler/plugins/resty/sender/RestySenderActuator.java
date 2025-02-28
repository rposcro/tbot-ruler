package com.tbot.ruler.plugins.resty.sender;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.broker.payload.BinaryState;
import com.tbot.ruler.exceptions.MessageProcessingException;
import com.tbot.ruler.subjects.actuator.AbstractActuator;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.time.Duration;
import java.util.Date;

@Slf4j
public class RestySenderActuator extends AbstractActuator {

    private final static int TIMEOUT_CONNECTION = 3000;
    private final static int TIMEOUT_READ = 5000;

    private final RestySenderConfiguration restySenderConfiguration;
    private final RestTemplate restTemplate;

    @Builder
    public RestySenderActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull RestySenderConfiguration restySenderConfiguration) {
        super(uuid, name, description);
        this.restySenderConfiguration = restySenderConfiguration;
        this.restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(TIMEOUT_CONNECTION))
            .setReadTimeout(Duration.ofMillis(TIMEOUT_READ))
            .build();
    }

    @Override
    public void acceptMessage(Message message) {
        consumeMessage(message, BinaryState.class, this::consumeOnOffMessage);
    }

    private void consumeOnOffMessage(Message message) {
        BinaryState binaryState = message.getPayloadAs(BinaryState.class);

        if (!binaryState.isOn()) {
            log.info("Received Off state from {}, ignoring it", message.getSenderId());
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            restySenderConfiguration.getHeaders().forEach(headers::add);
            String body = renderRequestBody();
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                restySenderConfiguration.getUrl(),
                HttpMethod.valueOf(restySenderConfiguration.getMethod()),
                entity,
                String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new MessageProcessingException("Resty sender actuator failed to send request by status " + response.getStatusCode());
            }

            log.info("Resty sender actuator successfully sent request, and received response {}", response.getBody());
        } catch (Exception e) {
            throw new MessageProcessingException("Resty sender actuator failed to send request by an exception", e);
        }
    }

    private String renderRequestBody() {
        String body = restySenderConfiguration.getBody();
        body = body.replaceAll("\\{date\\}", new Date().toString());
        body = UriUtils.encodeQuery(body, "UTF-8");
        return body;
    }
}
