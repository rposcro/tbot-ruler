package com.tbot.ruler.subjects.webhook;

import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.model.MessagePublicationReport;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class Webhook implements MessageSender {

    @NonNull
    private String uuid;
    @NonNull
    private String name;
    @NonNull
    private String owner;
    private String description;

    @Override
    public void acceptPublicationReport(MessagePublicationReport publicationReport) {
        log.debug("Webhook {} received publication report", uuid);
    }
}
