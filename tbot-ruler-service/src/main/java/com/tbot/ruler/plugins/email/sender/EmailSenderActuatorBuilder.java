package com.tbot.ruler.plugins.email.sender;

import com.tbot.ruler.exceptions.PluginException;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.subjects.plugin.RulerPluginContext;
import com.tbot.ruler.plugins.email.EmailActuatorBuilder;
import com.tbot.ruler.plugins.email.EmailSenderConfiguration;
import com.tbot.ruler.subjects.actuator.Actuator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.tbot.ruler.subjects.plugin.PluginsUtil.parseConfiguration;

public class EmailSenderActuatorBuilder extends EmailActuatorBuilder {

    public EmailSenderActuatorBuilder() {
        super("sender");
    }

    @Override
    public Actuator buildActuator(
            ActuatorEntity actuatorEntity,
            RulerPluginContext rulerPluginContext,
            EmailSenderConfiguration emailSenderConfiguration) throws PluginException {
        EmailConfiguration emailConfiguration = parseConfiguration(actuatorEntity.getConfiguration(), EmailConfiguration.class);
        JavaMailSender emailSender = buildEmailSender(emailSenderConfiguration);
        return EmailSenderActuator.builder()
                .uuid(actuatorEntity.getActuatorUuid())
                .name(actuatorEntity.getName())
                .description(actuatorEntity.getDescription())
                .mailSender(emailSender)
                .emailConfiguration(emailConfiguration)
                .build();
    }

    private JavaMailSender buildEmailSender(EmailSenderConfiguration configuration) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configuration.getMailSenderHost());
        mailSender.setPort(configuration.getMailSenderPort());
        mailSender.setUsername(configuration.getMailSenderUserName());
        mailSender.setPassword(configuration.getMailSenderPassword());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "true");

        return mailSender;
    }
}
