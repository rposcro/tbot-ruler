package com.tbot.ruler.message;

import com.tbot.ruler.message.Message;

import java.util.function.Consumer;

@FunctionalInterface
public interface MessagePublisher extends Consumer<Message> {
}
