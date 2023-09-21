package com.tbot.ruler.service.things;

import com.tbot.ruler.broker.MessageSender;
import com.tbot.ruler.broker.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BindingsService {

    @Autowired
    private BindingsLifetimeService bindingsLifetimeService;

    @Autowired
    private SubjectLifetimeService subjectLifetimeService;

    @Autowired
    private AppliancesLifetimeService appliancesLifetimeService;

    public Collection<String> findReceiversUuidsBySenderUuid(String senderUuid) {
        return bindingsLifetimeService.getReceiversForSender(senderUuid);
    }

    public MessageReceiver findReceiverByUuid(String receiverUuid) {
        return actuatorOrAppliance(receiverUuid);
    }

    public MessageSender findSenderByUuid(String senderUuid) {
        return actuatorOrAppliance(senderUuid);
    }

    private <T> T actuatorOrAppliance(String uuid) {
        T subject = (T) subjectLifetimeService.getActuatorByUuid(uuid);
        if (subject == null) {
            subject = (T) appliancesLifetimeService.getApplianceByUuid(uuid);
        }
        return subject;
    }
}
