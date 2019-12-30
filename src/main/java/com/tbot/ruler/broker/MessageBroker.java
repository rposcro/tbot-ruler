package com.tbot.ruler.broker;

import com.tbot.ruler.exceptions.MessageException;
import com.tbot.ruler.service.things.BindingsService;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.things.service.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
public class MessageBroker implements Runnable {

    @Autowired
    private MessageQueue messageQueue;

    @Autowired
    private BindingsService bindingsService;

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messageQueue.nextMessage();
                Collection<MessageConsumer> consumers = bindingsService.findBindedMessageConsumers(message.getSenderId());
                if (consumers.isEmpty()) {
                    log.info("No bindings found for messages from {}", message.getSenderId().getValue());
                } else {
                    consumers.stream()
                        .forEach(collector -> {
                            try {
                                this.deliverMessage(message, collector);
                                log.debug("Dispatched message from {}", message.getSenderId().getValue());
                            } catch(MessageException e) {
                                log.error("Consumer failed to process message from " + message.getSenderId().getValue(), e);
                            }
                        });
                }
            } catch(Exception e) {
                log.error("Message dispatching interrupted", e);
            }
        }
    }

    private void deliverMessage(Message message, MessageConsumer messageConsumer) {
        messageConsumer.acceptMessage(message);
    }
}
