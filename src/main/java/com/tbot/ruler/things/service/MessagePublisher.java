package com.tbot.ruler.things.service;

import com.tbot.ruler.message.Message;

import java.util.function.Consumer;

@FunctionalInterface
public interface MessagePublisher extends Consumer<Message> {
}
