package com.tbot.ruler.message;

import java.util.function.Consumer;

@FunctionalInterface
public interface MessagePublisher extends Consumer<Message> {
}
