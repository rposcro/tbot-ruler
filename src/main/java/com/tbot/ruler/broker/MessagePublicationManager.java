package com.tbot.ruler.broker;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MessagePublicationManager {

    private final Set<String> suspendedSenders;

    public MessagePublicationManager() {
        this.suspendedSenders = new HashSet<>();
    }

    public void suspendMessagesFrom(String senderId) {
        suspendedSenders.add(senderId);
    }

    public void unsuspendMessagesFrom(String senderId) {
        suspendedSenders.remove(senderId);
    }

    public Set<String> getSuspendedSenders() {
        return Collections.unmodifiableSet(suspendedSenders);
    }

    public boolean isSenderSuspended(String senderId) {
        return suspendedSenders.contains(senderId);
    }
}
