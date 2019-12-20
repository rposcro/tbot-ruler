package com.tbot.ruler.broker;

import com.tbot.ruler.things.Collector;
import com.tbot.ruler.message.Message;
import com.tbot.ruler.newy.services.ItemBindingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageBroker implements Runnable {

    @Autowired
    private MessageQueue messageQueue;

    @Autowired
    private ItemBindingsService itemBindingsService;

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messageQueue.nextMessage();
                itemBindingsService.findBindedCollectors(message.getSenderId()).stream()
                    .forEach(collector -> this.deliverMessage(message, collector));
            } catch(InterruptedException e) {
            } catch(Exception e) {
            }
        }
    }

    private void deliverMessage(Message message, Collector collector) {
        collector.acceptMessage(message);
    }
}
