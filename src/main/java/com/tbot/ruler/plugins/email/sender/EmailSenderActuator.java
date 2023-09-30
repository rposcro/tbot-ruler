package com.tbot.ruler.plugins.email.sender;

import com.tbot.ruler.broker.model.Message;
import com.tbot.ruler.subjects.AbstractActuator;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

public class EmailSenderActuator extends AbstractActuator {

    private final JavaMailSender mailSender;
    private final EmailConfiguration emailConfiguration;

    @Builder
    public EmailSenderActuator(
            @NonNull String uuid,
            @NonNull String name,
            String description,
            @NonNull JavaMailSender mailSender,
            @NonNull EmailConfiguration emailConfiguration) {
        super(uuid, name, description);
        this.mailSender = mailSender;
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public void acceptMessage(Message message) {
        String emailBody = renderEmailBody(message);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(emailConfiguration.getEmailFrom());
        email.setTo(emailConfiguration.getEmailTo());
        email.setSubject(emailConfiguration.getEmailTitle());
        email.setText(emailBody);
        mailSender.send(email);
    }

    private String renderEmailBody(Message message) {
        Date now = new Date();
        String emailBody = emailConfiguration.getEmailBodyTemplate();

        emailBody = emailBody.replaceAll("\\{date\\}", now.toString());
        emailBody = emailBody.replaceAll("\\{message\\}", message.getPayload().toString());

        return emailBody;
    }
}