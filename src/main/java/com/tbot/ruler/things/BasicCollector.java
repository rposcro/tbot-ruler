package com.tbot.ruler.things;

import com.tbot.ruler.message.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.Consumer;

@Getter
public class BasicCollector extends AbstractItem<CollectorId> implements Collector {

    private Consumer<Message> messageCollectorConsumer;

    @Builder
    public BasicCollector(
        @NonNull CollectorId id,
        @NonNull String name,
        String description,
        @NonNull Consumer<Message> messageCollectorConsumer
    ) {
        super(id, name, description);
        this.messageCollectorConsumer = messageCollectorConsumer;
    }

    @Override
    public void acceptMessage(Message message) {
        messageCollectorConsumer.accept(message);
    }
}
