package com.tbot.ruler.plugins.notifier.email;

import com.tbot.ruler.plugins.ghost.singleinterval.SingleIntervalAgent;
import com.tbot.ruler.subjects.AbstractActuator;
import com.tbot.ruler.task.Task;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Collection;

public class EmailNotifierActuator extends AbstractActuator {

    private final JavaMailSender mailSender;

    @Builder
    public EmailNotifierActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull SingleIntervalAgent singleIntervalAgent,
            @NonNull JavaMailSender mailSender) {
        super(uuid, name, description);
        this.mailSender = mailSender;
//        this.singleIntervalAgent = singleIntervalAgent;
    }


}
