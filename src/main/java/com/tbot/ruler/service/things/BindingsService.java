package com.tbot.ruler.service.things;

import com.tbot.ruler.exceptions.ConfigurationException;
import com.tbot.ruler.messages.MessageSender;
import com.tbot.ruler.messages.MessageReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BindingsService {

    @Autowired
    private BindingsLifetimeService bindingsLifetimeService;

    @Autowired
    private ThingsLifetimeService thingsLifetimeService;

    @Autowired
    private AppliancesLifetimeService appliancesLifetimeService;

    public Collection<String> findReceiversUuidsBySenderUuid(String senderUuid) {
        return bindingsLifetimeService.getReceiversForSender(senderUuid);
    }

    public MessageReceiver findReceiverByUuid(String receiverUuid) {
        MessageReceiver messageReceiver = actuatorOrAppliance(receiverUuid);
        if (messageReceiver == null) {
            throw new ConfigurationException("Configuration inconsistent, no receiver found for uuid " + receiverUuid);
        }
        return messageReceiver;
    }

    public MessageSender findSenderByUuid(String senderUuid) {
        MessageSender messageSender = actuatorOrAppliance(senderUuid);
        if (messageSender == null) {
            throw new ConfigurationException("Configuration inconsistent, no sender found for uuid " + messageSender);
        }
        return messageSender;
    }

    private <T> T actuatorOrAppliance(String uuid) {
        T item = (T) thingsLifetimeService.getActuatorByUuid(uuid);
        if (item == null) {
            item = (T) appliancesLifetimeService.getApplianceByUuid(uuid);
        }
        return item;
    }
}
