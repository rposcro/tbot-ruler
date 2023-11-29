package com.tbot.ruler.service.things;

import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.MessageReceiver;
import com.tbot.ruler.service.lifecycle.BindingsLifecycleService;
import com.tbot.ruler.service.lifecycle.SubjectLifecycleService;
import com.tbot.ruler.service.lifecycle.WebhooksLifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BindingsService {

    @Autowired
    private BindingsLifecycleService bindingsLifecycleService;

    @Autowired
    private SubjectLifecycleService subjectLifecycleService;

    @Autowired
    private WebhooksLifecycleService webhooksLifecycleService;

    public Collection<String> findReceiversUuidsBySenderUuid(String senderUuid) {
        return bindingsLifecycleService.getReceiversForSender(senderUuid);
    }

    public MessageReceiver findReceiverByUuid(String receiverUuid) {
        return subjectLifecycleService.getActuatorByUuid(receiverUuid);
    }

    public MessageSender findSenderByUuid(String senderUuid) {
        MessageSender sender = subjectLifecycleService.getActuatorByUuid(senderUuid);
        if (sender == null) {
            sender = webhooksLifecycleService.getWebhookByUuid(senderUuid);
        }
        return sender;
    }
}
